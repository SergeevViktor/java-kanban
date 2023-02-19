package tests.taskManagerTests;

import manager.taskManagers.InMemoryTaskManager;
import manager.taskManagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    @BeforeEach
    void beforeEach(){
        super.beforeEach();
        manager = new InMemoryTaskManager();
    }
}