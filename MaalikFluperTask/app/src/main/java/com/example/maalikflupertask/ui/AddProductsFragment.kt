package com.example.maalikflupertask.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.maalikflupertask.R
import com.example.maalikflupertask.db.Product
import com.example.maalikflupertask.db.ProductDatabase
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddProductsFragment : BaseFragment() {

    lateinit var imageView: ImageView
    val CAMERA_REQUEST_CODE = 111
    val GALLERY_REQUEST_CODE = 112

    private val PERMISSION_REQUEST_CAMERA: Int = 101

    private val PERMISSION_REQUEST_GALLERY: Int = 102
    private var mCurrentPhotoPath: String? = null;
    private var product: Product? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            product = AddProductsFragmentArgs.fromBundle(it).product
            edit_text_name.setText(product?.name)
            edit_text_details.setText(product?.detail)
            edit_text_price.setText(product?.price)
            mCurrentPhotoPath = product?.image
            Glide.with(this)
                .load(mCurrentPhotoPath)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(image_product)


        }

        cameraBtn.setOnClickListener(View.OnClickListener {

            checkPersmission(true)

        })

        galleryBtn.setOnClickListener(View.OnClickListener {

 
            checkPersmission(false)


        })



        button_save.setOnClickListener { view ->

            val productName = edit_text_name.text.toString().trim()
            val productDetail = edit_text_details.text.toString().trim()
            val productPrice = edit_text_price.text.toString().trim()

            if (productName.isEmpty()) {
                edit_text_name.error = "name required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            if (productDetail.isEmpty()) {
                edit_text_details.error = "details required"
                edit_text_details.requestFocus()
                return@setOnClickListener
            }

            if (productPrice.isEmpty()) {
                edit_text_price.error = "price required"
                edit_text_price.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                Toast.makeText(requireActivity(), "Select An Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            launch {

                context?.let {
                    val mNote = Product(productName, productDetail, productPrice, mCurrentPhotoPath!!)

                    if (product == null) {
                        ProductDatabase(it).getProductDao().addProduct(mNote)
                        it.toast("Product Saved")
                    } else {
                        mNote.id = product!!.id
                        ProductDatabase(it).getProductDao().updateProduct(mNote)
                        it.toast("Product Updated")
                    }


                    val action = AddProductsFragmentDirections.actionSaveProduct()
                    Navigation.findNavController(view).navigate(action)
                }
            }

        }

    }

    private fun deleteNote() {
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure?")
            setMessage("You cannot undo this operation")
            setPositiveButton("Yes") { _, _ ->
                launch {
                    ProductDatabase(context).getProductDao().deleteProduct(product!!)
                    val action = AddProductsFragmentDirections.actionSaveProduct()
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
            setNegativeButton("No") { _, _ ->

            }
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_menu -> if (product != null) deleteNote() else context?.toast("Cannot Delete")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val f  = File(mCurrentPhotoPath);


                Glide.with(this)
                    .load(Uri.fromFile(f))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(image_product)


            }

        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var contentUri: Uri = data?.getData()!!;
                Glide.with(this)
                    .load(contentUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(image_product)

                mCurrentPhotoPath = contentUri.toString()
            }


        }

    }


    private fun checkPersmission(isCamera: Boolean) {
        if (isCamera){
            if(ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ){
                requestPermission(isCamera);
            }else {
                dispatchTakePictureIntent();
            }

        }

        else {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                requestPermission(isCamera)
            }
            else{
                val gallery: Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }


        }
    }

    private fun requestPermission(isCamera: Boolean) {
        if (isCamera)
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISSION_REQUEST_CAMERA
            )
        else
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_GALLERY
            )

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Camera & Storage Permission Required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (requestCode == PERMISSION_REQUEST_GALLERY) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val gallery: Intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Read Storage Permission Required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }



}