package com.shevelev.wizard_camera.gallery_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shevelev.wizard_camera.R
import com.shevelev.wizard_camera.application.App
import com.shevelev.wizard_camera.common_entities.entities.PhotoShot
import com.shevelev.wizard_camera.databinding.FragmentGalleryPageBinding
import com.shevelev.wizard_camera.gallery_page.di.GalleryPageFragmentComponent
import com.shevelev.wizard_camera.shared.files.FilesHelper
import com.shevelev.wizard_camera.shared.glide.GlideTarget
import com.shevelev.wizard_camera.shared.glide.clear
import com.shevelev.wizard_camera.shared.glide.load
import com.shevelev.wizard_camera.shared.mvvm.view.FragmentBase
import com.shevelev.wizard_camera.utils.useful_ext.ifNotNull
import javax.inject.Inject

class GalleryPageFragment : FragmentBase() {
    companion object {
        private const val ARG_PHOTO = "PHOTO"

        fun newInstance(item: PhotoShot): GalleryPageFragment =
            GalleryPageFragment().apply { arguments = Bundle().apply { putParcelable(ARG_PHOTO, item) } }
    }

    private var _binding: FragmentGalleryPageBinding? = null
    private val binding: FragmentGalleryPageBinding
        get() = _binding!!

    @Inject
    internal lateinit var filesHelper: FilesHelper

    private var glideCancel: GlideTarget? = null

    override fun inject(key: String) = App.injections.get<GalleryPageFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<GalleryPageFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val photo = arguments!!.getParcelable<PhotoShot>(ARG_PHOTO)!!
        glideCancel = binding.imageContainer.load(filesHelper.getShotFileByName(photo.fileName), R.drawable.ic_sad_face)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        ifNotNull(glideCancel, context) { glideCancel, context ->
            glideCancel.clear(context)
        }

        _binding = null
    }
}