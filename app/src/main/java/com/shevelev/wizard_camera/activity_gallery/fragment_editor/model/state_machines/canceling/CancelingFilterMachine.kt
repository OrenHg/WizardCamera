package com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.canceling

import com.shevelev.wizard_camera.activity_gallery.fragment_editor.model.state_machines.*
import com.shevelev.wizard_camera.shared.coroutines.DispatchersProvider
import kotlinx.coroutines.flow.MutableSharedFlow

class CancelingFilterMachine(
    outputCommands: MutableSharedFlow<OutputCommand>,
    dispatchersProvider: DispatchersProvider
) : EditorMachineBase(outputCommands, dispatchersProvider) {

    override suspend fun processEvent(event: InputEvent, state: State): State =
        when {
            state == State.INITIAL && event is Init -> {
                // If the image is updated {
                outputCommands.emit(ShowSaveDialog)
                 State.SAVE_DIALOG_IS_SHOWN
                // } else {
                outputCommands.emit(CloseEditor)
                State.FINAL
                // }
            }

            state == State.SAVE_DIALOG_IS_SHOWN && event is CancelClicked -> {
                outputCommands.emit(CloseEditor)
                State.FINAL
            }

            state == State.SAVE_DIALOG_IS_SHOWN && event is AcceptClicked -> {
                // Update an image in the storage
                outputCommands.emit(CloseEditor)
                State.FINAL
            }

            else -> state
        }
}