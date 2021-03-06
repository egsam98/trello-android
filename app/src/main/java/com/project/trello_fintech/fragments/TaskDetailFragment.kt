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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.BR
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.AttachmentsAdapter
import com.project.trello_fintech.adapters.ChecklistsAdapter
import com.project.trello_fintech.adapters.CommentsAdapter
import com.project.trello_fintech.adapters.ParticipantsAdapter
import com.project.trello_fintech.fragments.dialogs.CheckitemDialogFragment
import com.project.trello_fintech.fragments.dialogs.ChecklistDialogFragment
import com.project.trello_fintech.fragments.dialogs.ParticipantsDialogFragment
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.TrelloUtil
import com.project.trello_fintech.utils.getVCSLogo
import com.project.trello_fintech.utils.observe
import com.project.trello_fintech.utils.toDefaultFormat
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
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

@BindingAdapter("app:vcsUrl")
fun ImageView.setVCSLogo(vcsUrl: String?) {
    vcsUrl?.let {
        setImageDrawable(resources.getVCSLogo(it))
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
class TaskDetailFragment: Fragment(), DrawerMenuOwner {

    companion object {
        private const val TASK_ARG = "task"
        private const val TASK_ID_ARG = "taskId"
        fun create(task: Task): TaskDetailFragment {
            val bundle = Bundle().apply { putSerializable(TASK_ARG, task) }
            return TaskDetailFragment().apply { arguments = bundle }
        }
        fun create(taskId: String): TaskDetailFragment {
            val bundle = Bundle().apply { putString(TASK_ID_ARG, taskId) }
            return TaskDetailFragment().apply { arguments = bundle }
        }
    }

    @Inject lateinit var cxt: Context
    @Inject lateinit var activity: MainActivity
    @Inject lateinit var cleanableViewModelProvider: CleanableViewModelProvider
    @Inject lateinit var trelloUtil: TrelloUtil

    private var attachmentsAdapter: AttachmentsAdapter? = null
    private var binding: ViewDataBinding? = null

    val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(viewLifecycleOwner)
    }

    private var requestPermissionsUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_detail, container, false)
        binding!!.lifecycleOwner = this
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.component.inject(this)
        val args = requireArguments()
        val boardBackgroundView = view.findViewById<AppBarLayout>(R.id.app_bar)
        args.getSerializable(TASK_ARG)
            ?.let { taskDetailViewModel.attachTask(it as Task, boardBackgroundView) }
            ?: run { args.getString(TASK_ID_ARG)?.let { taskDetailViewModel.attachTask(it, boardBackgroundView) } }

        binding?.setVariable(BR.viewModel, taskDetailViewModel)

        attachmentsAdapter = AttachmentsAdapter(taskDetailViewModel)

        view.findViewById<RecyclerView>(R.id.attachments).apply {
            layoutManager = FlexboxLayoutManager(cxt, FlexDirection.ROW).apply {
                justifyContent = JustifyContent.SPACE_AROUND
            }
            adapter = attachmentsAdapter
        }

        taskDetailViewModel.observeAttachments {
            attachmentsAdapter?.data = it
        }

        view.findViewById<Button>(R.id.task_history).apply {
            setOnClickListener {
                taskDetailViewModel.showHistory()
            }
        }

        val checklistsAdapter = ChecklistsAdapter(this)
        view.findViewById<RecyclerView>(R.id.checklists).apply { adapter = checklistsAdapter }

        val participantsAdapter = ParticipantsAdapter(this)
        view.findViewById<RecyclerView>(R.id.participants).apply {
            layoutManager = LinearLayoutManager(cxt, RecyclerView.HORIZONTAL, false)
            adapter = participantsAdapter
        }

        val commentsAdapter = CommentsAdapter()
        view.findViewById<RecyclerView>(R.id.comments).apply {
            layoutManager = LinearLayoutManager(cxt)
            adapter = commentsAdapter
        }

        val commentInputText = view.findViewById<TextInputEditText>(R.id.comment_input).text
        view.findViewById<Button>(R.id.add_comment).setOnClickListener {
            taskDetailViewModel.addComment(commentInputText.toString()) { commentInputText?.clear() }
        }

        taskDetailViewModel.checklists.observe(viewLifecycleOwner) {
            checklistsAdapter.data = it
        }
        taskDetailViewModel.participants.observe(viewLifecycleOwner) {
            participantsAdapter.data = it
        }
        taskDetailViewModel.firebaseData.comments.observe(viewLifecycleOwner) {
            commentsAdapter.data = it
        }

        activity.navigationView.setupMenu()
    }

    override fun onPause() {
        super.onPause()
        taskDetailViewModel.updateInputs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        attachmentsAdapter = null
        binding = null
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

    override fun NavigationView.setupMenu() {
        inflateMenu(R.menu.task_detail)
        setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.add_checklist -> showChecklistDialog()
                R.id.add_attachment -> addAttachment()
            }
            true
        }
    }

    fun selectParticipants() {
        val task = taskDetailViewModel.task.value!!
        val fragment = ParticipantsDialogFragment.create(task)
        fragment.show(childFragmentManager, null)
    }

    fun showChecklistDialog(checklist: Checklist? = null) {
        val fragment = ChecklistDialogFragment.create(checklist)
        fragment.show(childFragmentManager, null)
    }

    fun showCheckitemDialog(checklist: Checklist, checkitem: Checklist.Item? = null) {
        val fragment = CheckitemDialogFragment.create(checklist, checkitem)
        fragment.show(childFragmentManager, null)
    }

    private fun addAttachment() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "*/*" }
        startActivityForResult(intent, UPLOAD_ATTACHMENT_REQUEST)
    }
}