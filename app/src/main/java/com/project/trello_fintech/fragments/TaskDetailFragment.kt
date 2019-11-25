package com.project.trello_fintech.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.trello_fintech.BR
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.AttachmentsAdapter
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import com.project.trello_fintech.views.DropDownListView
import javax.inject.Inject


private const val READ_EXTERNAL_STORAGE_PERM = android.Manifest.permission.READ_EXTERNAL_STORAGE
private const val UPLOAD_ATTACHMENT_REQUEST = 1
private const val READ_EXTERNAL_STORAGE_REQUEST = 2


@BindingAdapter("subtitle")
fun Toolbar.setSubtitle(loadingInfo: LiveData<MutableList<String>>) {
    subtitle = loadingInfo.value?.joinToString(", ")
}

/**
 * Фрагмент детальной информации выбранной задачи
 * @property cxt Context
 * @property activity MainActivity
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property binding ViewDataBinding
 * @property taskDetailViewModel TaskDetailViewModel
 * @property requestPermissionsUri Uri?
 */
class TaskDetailFragment: Fragment() {

    @Inject
    lateinit var cxt: Context

    @Inject
    lateinit var activity: MainActivity

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private lateinit var binding: ViewDataBinding

    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(this)
    }

    private var requestPermissionsUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_task_detail, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.component.inject(this)
        binding.setVariable(BR.viewModel, taskDetailViewModel)

        val attachmentsAdapter = AttachmentsAdapter(taskDetailViewModel)
        taskDetailViewModel.observeAttachments {
            attachmentsAdapter.data = it
        }

        taskDetailViewModel.attachTask(arguments?.getSerializable("task") as Task)

        view.findViewById<RecyclerView>(R.id.attachments).apply {
            layoutManager = FlexboxLayoutManager(cxt, FlexDirection.ROW).apply {
                justifyContent = JustifyContent.SPACE_AROUND
            }
            adapter = attachmentsAdapter
        }

        val taskActionsView = view.findViewById<DropDownListView>(R.id.task_actions).apply {
            adapter = ArrayAdapter(cxt, android.R.layout.simple_list_item_1, taskDetailViewModel.actionsTitle)
            setOnItemClickListener { _, _, i, _ ->
                openClose()
                taskDetailViewModel.actions[i]?.emit()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.task_action).setOnClickListener {
            taskActionsView.openClose()
        }

        taskDetailViewModel.actions[0]?.observe(this, Observer {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
            }
            startActivityForResult(intent, UPLOAD_ATTACHMENT_REQUEST)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            UPLOAD_ATTACHMENT_REQUEST -> intent?.data?.let {
                if (cxt.checkSelfPermission(READ_EXTERNAL_STORAGE_PERM) == PackageManager.PERMISSION_GRANTED)
                    taskDetailViewModel.uploadAttachment(it)
                else {
                    activity.requestPermissions(arrayOf(READ_EXTERNAL_STORAGE_PERM), READ_EXTERNAL_STORAGE_REQUEST)
                    requestPermissionsUri = it
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissionsUri?.let { taskDetailViewModel.uploadAttachment(it) }
                }
            }
        }
    }
}