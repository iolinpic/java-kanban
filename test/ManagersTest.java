import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void shouldGenerateNotNullTaskManager() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm);
    }

    @Test
    void shouldGenerateNotNullHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}