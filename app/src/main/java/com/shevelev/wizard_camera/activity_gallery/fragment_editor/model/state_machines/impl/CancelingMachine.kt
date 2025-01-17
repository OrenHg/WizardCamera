package com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.impl

import com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.api.*
import com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.impl.EditorMachineBase
import com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.impl.State
import com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.storage.EditorStorage
import com.shevelev.wizard_camera.shared.coroutines.DispatchersProvider
import kotlinx.coroutines.flow.MutableSharedFlow

class CancelingMachine(
    outputCommands: MutableSharedFlow<OutputCommand>,
    dispatchersProvider: DispatchersProvider,
    editorStorage: EditorStorage
) : EditorMachineBase(outputCommands, dispatchersProvider, editorStorage) {

    override suspend fun processEvent(event: InputEvent, state: State): State =
        when {
            state == State.INITIAL && event is Init -> {
                if(editorStorage.isUpdated) {
                    outputCommands.emit(ShowSaveDialog)
                    State.SAVE_DIALOG_IS_SHOWN
                } else {
                    outputCommands.emit(CloseEditor)
                    State.FINAL
                }
            }

            state == State.SAVE_DIALOG_IS_SHOWN && event is CancelClicked -> {
                outputCommands.emit(CloseEditor)
                State.FINAL
            }

            state == State.SAVE_DIALOG_IS_SHOWN && event is AcceptClicked -> {
                saveImage()
                outputCommands.emit(CloseEditor)
                State.FINAL
            }

            else -> state
        }
}