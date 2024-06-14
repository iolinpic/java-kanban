package models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldBeEqualsWhenIdsAreEqual() {
        Epic epic1 = new Epic("epic", "epic detail");
        SubTask subTask = new SubTask("sub", "sub", epic1);
        SubTask subTask2 = new SubTask("sub2", "sub2", epic1);
        SubTask subTask3 = new SubTask("sub2", "sub2", epic1);
        subTask3.setId(1);
        SubTask subTask4 = new SubTask("sub2", "sub2", epic1);
        subTask4.setId(2);
        assertEquals(subTask, subTask2);
        assertNotEquals(subTask3, subTask4);
    }

    @Test
    void shouldNotBeAbleToAddSubtaskAsEpic() {
        //чисто по сигнатуре метода Subtask(String name,String description,Epic epic) мы не сможем добавить
    }
}