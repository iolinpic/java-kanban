package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldBeEqualsWhenIdsAreEqual() {
        Epic epic1 = new Epic("epic", "epic detail");
        SubTask subTask = new SubTask("sub", "sub", epic1.getId());
        SubTask subTask2 = new SubTask("sub2", "sub2", epic1.getId());
        SubTask subTask3 = new SubTask("sub2", "sub2", epic1.getId());
        subTask3.setId(1);
        SubTask subTask4 = new SubTask("sub2", "sub2", epic1.getId());
        subTask4.setId(2);
        assertEquals(subTask, subTask2);
        assertNotEquals(subTask3, subTask4);
    }
}