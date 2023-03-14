package tests.taskManagerTests;

import main.taskManagers.InMemoryTaskManager;
import main.taskManagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    @BeforeEach
    void beforeEach(){
        super.beforeEach();
        manager = new InMemoryTaskManager();
    }
}