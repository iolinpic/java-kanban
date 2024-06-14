import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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