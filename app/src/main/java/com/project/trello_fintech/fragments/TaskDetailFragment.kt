package com.project.trello_fintech.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.BR
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.AttachmentsAdapter
import com.project.trello_fintech.adapters.ParticipantsAdapter
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.toDefaultFormat
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import com.project.trello_fintech.views.DropDownListView
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject


private const val READ_EXTERNAL_STORAGE_PERM = android.Manifest.permission.READ_EXTERNAL_STORAGE
private const val UPLOAD_ATTACHMENT_REQUEST = 1
private const val READ_EXTERNAL_STORAGE_REQUEST = 2

@BindingAdapter("android:text")
fun TextView.setDate(date: Date?) {
    text = when (id) {
        R.id.task_date_start -> "Начало: ${date.toDefaultFormat()}"
        R.id.task_date_deadline -> "Дедлайн: ${date.toDefaultFormat()}"
        else -> throw IllegalStateException("Неизвестный TextView ID")
    }
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

    companion object {
        private const val TASK_ARG = "task"
        fun create(task: Task): TaskDetailFragment {
            val bundle = Bundle().apply { putSerializable(TASK_ARG, task) }
            return TaskDetailFragment().apply { arguments = bundle }
        }
    }

    @Inject
    lateinit var cxt: Context

    @Inject
    lateinit var activity: MainActivity

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private lateinit var attachmentsAdapter: AttachmentsAdapter

    private lateinit var binding: ViewDataBinding

    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(viewLifecycleOwner)
    }

    private var requestPermissionsUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_task_detail, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.component.inject(this)
        requireArguments().getSerializable(TASK_ARG)?.let { taskDetailViewModel.attachTask(it as Task) }

        binding.setVariable(BR.viewModel, taskDetailViewModel)

        attachmentsAdapter = AttachmentsAdapter(taskDetailViewModel)

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
        taskDetailViewModel.observeAttachments {
            attachmentsAdapter.data = it
        }

        view.findViewById<TextInputEditText>(R.id.description).apply {
            val originalDrawable = background
            background = null
            setOnFocusChangeListener { _, isFocused ->
                val drawable = when (isFocused) {
                    true -> originalDrawable
                    false -> null
                }
                background = drawable
            }
        }

        view.findViewById<Button>(R.id.task_history).apply {
            setOnClickListener {
                taskDetailViewModel.showHistory()
            }
        }

        val participantsAdapter = ParticipantsAdapter(this)
        view.findViewById<RecyclerView>(R.id.participants).apply {
            layoutManager = LinearLayoutManager(cxt, RecyclerView.HORIZONTAL, false)
            adapter = participantsAdapter
        }

        taskDetailViewModel.participants.observe(viewLifecycleOwner, Observer {
            participantsAdapter.data = it
        })
    }

    override fun onPause() {
        super.onPause()
        taskDetailViewModel.updateDescription()
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

    fun selectParticipants() {
        val task = taskDetailViewModel.task.value!!
        val fragment = ParticipantsDialogFragment.create(task)
        fragment.show(childFragmentManager, null)
    }
}