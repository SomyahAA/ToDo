import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.todolist.dataBase.Task
import com.example.todolist.dataBase.TaskRepository
import java.util.*


class TaskFragmentViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    private val taskIdLiveData = MutableLiveData<UUID>()


    var taskLiveData: LiveData<Task?> =
        Transformations.switchMap(taskIdLiveData) {
            taskRepository.getTask(it)
        }

    fun loadTask(taskId: UUID) {
        taskIdLiveData.value = taskId
    }

    fun saveUpdate(task: Task) {
        taskRepository.updateTask(task)
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }


}