package com.shevelev.wizard_camera.activity_gallery.fragment_gallery.view_model

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevelev.wizard_camera.R
import com.shevelev.wizard_camera.activity_gallery.fragment_gallery.dto.EditShotCommand
import com.shevelev.wizard_camera.activity_gallery.fragment_gallery.dto.GalleryItem
import com.shevelev.wizard_camera.activity_gallery.fragment_gallery.dto.ShareShotCommand
import com.shevelev.wizard_camera.activity_gallery.fragment_gallery.dto.ShotsLoadingResult
import com.shevelev.wizard_camera.activity_gallery.fragment_gallery.model.GalleryFragmentInteractor
import com.shevelev.wizard_camera.shared.coroutines.DispatchersProvider
import com.shevelev.wizard_camera.shared.mvvm.view_commands.ScrollGalleryToPosition
import com.shevelev.wizard_camera.shared.mvvm.view_commands.ShowMessageResCommand
import com.shevelev.wizard_camera.shared.mvvm.view_model.ViewModelBase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

class GalleryFragmentViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    interactor: GalleryFragmentInteractor
) : ViewModelBase<GalleryFragmentInteractor>(dispatchersProvider, interactor),
    GalleryPagingActions {

    private val _photos = MutableLiveData<List<GalleryItem>>()
    val photos: LiveData<List<GalleryItem>> = _photos

    val pageSize: Int = interactor.pageSize

    private val _isShareButtonVisible = MutableLiveData(View.INVISIBLE)
    val isShareButtonVisible: LiveData<Int> = _isShareButtonVisible

    private val _shotDateTime = MutableLiveData("")
    val shotDateTime: LiveData<String> = _shotDateTime

    private val _isNoDataStubVisible = MutableLiveData(View.INVISIBLE)
    val isNoDataStubVisible: LiveData<Int> = _isNoDataStubVisible

    private val _isShotWidgetsVisible = MutableLiveData(View.INVISIBLE)
    val isShotWidgetsVisible: LiveData<Int> = _isShotWidgetsVisible

    var currentPageIndex: Int? = null
        private set

    init {
        launch {
            interactor.loadingResult.collect {
                when(it) {
                    is ShotsLoadingResult.PreLoading -> {
                        _isNoDataStubVisible.value = View.INVISIBLE
                        _isShotWidgetsVisible.value = View.INVISIBLE
                        _isShareButtonVisible.value = View.INVISIBLE
                    }
                    is ShotsLoadingResult.DataUpdated -> {
                        _isNoDataStubVisible.value = if (it.data.isEmpty()) View.VISIBLE else View.INVISIBLE
                        _isShotWidgetsVisible.value = if (it.data.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                        _isShareButtonVisible.value = if (it.data.isNotEmpty()) View.VISIBLE else View.INVISIBLE

                        _photos.value = it.data
                    }
                }
            }
        }
    }

    override fun loadNextPage() = processAction { interactor.loadNextPage() }

    fun initPhotos() = processAction { interactor.initPhotos() }

    fun onDeleteShotClick(position: Int) = processAction { interactor.delete(position) }

    fun onShareShotClick(filteredImage: Bitmap) {
        launch {
            interactor.startBitmapSharing(filteredImage).also {
                _command.value = ShareShotCommand(it)
            }
        }
    }

    fun onShotSelected(position: Int) {
        val shot = interactor.getShot(position)

        _shotDateTime.value = shot.created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
        _isShareButtonVisible.value = shot.contentUri?.let { View.VISIBLE } ?: View.INVISIBLE

        currentPageIndex = position
    }

    fun onEditShotClick(position: Int) {
        _command.value = EditShotCommand(interactor.getShot(position))
    }

    fun startImageImport(uri: Uri, currentPosition: Int) {
        launch {
            if(!interactor.importBitmap(uri, currentPosition)) {
                _command.value = ShowMessageResCommand(R.string.generalError)
            } else {
                _command.value = ScrollGalleryToPosition(currentPosition)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        interactor.clear()
    }

    private fun processAction(action: suspend () -> Unit) {
        launch {
            try {
                action()
            } catch(ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.generalError)
            }
        }
    }
}