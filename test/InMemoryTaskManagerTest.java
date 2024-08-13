import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        init();
    }

    @Override
    void init() {
        taskManager = new InMemoryTaskManager();
    }
}