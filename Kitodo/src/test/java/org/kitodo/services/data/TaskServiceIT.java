/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.services.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.helper.enums.TaskStatus;

/**
 * Tests for TaskService class.
 */
public class TaskServiceIT {

    @BeforeClass
    public static void prepareDatabase() throws Exception {
        MockDatabase.insertProcessesFull();
    }

    @AfterClass
    public static void cleanDatabase() {
        // MockDatabase.cleanDatabase();
    }

    @Before
    public void multipleInit() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void shouldCountAllTasks() throws Exception {
        TaskService taskService = new TaskService();

        Long amount = taskService.count();
        assertEquals("Tasks were not counted correctly!", Long.valueOf(5), amount);
    }

    @Test
    public void shouldCountTasksAccordingToQuery() throws Exception {
        TaskService taskService = new TaskService();
        UserService userService = new UserService();

        Long amount = taskService.getAmountOfCurrentTasks(true, true, userService.find(1));
        assertEquals("Tasks were not counted correctly!", Long.valueOf(2), amount);

        amount = taskService.getAmountOfCurrentTasks(true, false, userService.find(1));
        assertEquals("Tasks were not counted correctly!", Long.valueOf(1), amount);

        amount = taskService.getAmountOfCurrentTasks(false, true, userService.find(1));
        assertEquals("Tasks were not counted correctly!", Long.valueOf(1), amount);

        amount = taskService.getAmountOfCurrentTasks(true, false, userService.find(2));
        assertEquals("Tasks were not counted correctly!", Long.valueOf(1), amount);
    }

    @Test
    public void shouldCountAllDatabaseRowsForTasks() throws Exception {
        TaskService taskService = new TaskService();

        Long amount = taskService.countDatabaseRows();
        assertEquals("Tasks were not counted correctly!", Long.valueOf(5), amount);
    }

    @Test
    public void shouldFindTask() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(1);
        boolean condition = task.getTitle().equals("Testing") && task.getPriority().equals(1);
        assertTrue("Task was not found in database!", condition);
    }

    @Test
    public void shouldFindAllTasks() {
        TaskService taskService = new TaskService();

        List<Task> tasks = taskService.findAll();
        assertEquals("Not all tasks were found in database!", 5, tasks.size());
    }

    @Test
    public void shouldFindByProcessingStatusAndUser() throws Exception {
        TaskService taskService = new TaskService();

        List<JSONObject> tasks = taskService.findByProcessingStatusAndUser(TaskStatus.INWORK, 1, null);
        assertEquals("Some tasks were found in database!", 0, tasks.size());

        tasks = taskService.findByProcessingStatusAndUser(TaskStatus.INWORK, 2, null);
        assertEquals("Not all tasks were found in database!", 1, tasks.size());

    }

    @Test
    public void shouldRemoveTask() throws Exception {
        TaskService taskService = new TaskService();

        Task task = new Task();
        task.setTitle("To Remove");
        task.setProcessingStatusEnum(TaskStatus.OPEN);
        taskService.save(task);
        Task foundTask = taskService.convertJSONObjectToBean(taskService.findById(6));
        assertEquals("Additional task was not inserted in database!", "To Remove", foundTask.getTitle());

        taskService.remove(foundTask);
        foundTask = taskService.convertJSONObjectToBean(taskService.findById(6));
        assertEquals("Additional task was not removed from database!", null, foundTask);

        task = new Task();
        task.setTitle("To remove");
        task.setProcessingStatusEnum(TaskStatus.OPEN);
        taskService.save(task);
        foundTask = taskService.convertJSONObjectToBean(taskService.findById(7));
        assertEquals("Additional task was not inserted in database!", "To remove", foundTask.getTitle());

        taskService.remove(7);
        foundTask = taskService.convertJSONObjectToBean(taskService.findById(7));
        assertEquals("Additional task was not removed from database!", null, foundTask);
    }

    @Test
    public void shouldGetProcessingBeginAsFormattedString() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(1);
        String expected = "2016-10-20 00:00:00";
        String actual = taskService.getProcessingBeginAsFormattedString(task);
        assertEquals("Processing time date is incorrect!", expected, actual);
    }

    @Test
    public void shouldGetProcessingTimeAsFormattedString() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(1);
        String expected = "2016-12-24 00:00:00";
        String actual = taskService.getProcessingTimeAsFormattedString(task);
        assertEquals("Processing time date is incorrect!", expected, actual);

        task = taskService.find(2);
        expected = "-";
        actual = taskService.getProcessingTimeAsFormattedString(task);
        assertEquals("Processing time date is incorrect!", expected, actual);
    }

    @Test
    public void shouldGetProcessingEndAsFormattedString() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(1);
        String expected = "2016-12-24 00:00:00";
        String actual = taskService.getProcessingEndAsFormattedString(task);
        assertEquals("Processing end date is incorrect!", expected, actual);
    }

    @Test
    public void shouldGetCorrectionStep() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(2);
        assertTrue("Task is not correction task!", taskService.isCorrectionStep(task));
    }

    @Test
    public void shouldGetNormalizedTitle() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(3);
        String expected = "Testing_and_Blocking";
        String actual = taskService.getNormalizedTitle(task.getTitle());
        assertEquals("Normalized title of task doesn't match given plain text!", expected, actual);
    }

    @Test
    public void shouldGetTitleWithUserName() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(1);
        String expected = "Testing (Kowalski, Jan)";
        String actual = taskService.getTitleWithUserName(task);
        assertEquals("Task's title with user name doesn't match given plain text!", expected, actual);

        task = taskService.find(3);
        expected = "Testing and Blocking";
        actual = taskService.getTitleWithUserName(task);
        assertEquals("Task's title with user name doesn't match given plain text!", expected, actual);
    }

    @Test
    public void shouldGetAllScriptPaths() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(2);
        String expected = "../type/automatic/script/path";
        String actual = taskService.getAllScriptPaths(task).get(0);
        assertEquals("Task's automatic script path doesn't match given plain text!", expected, actual);

        task = taskService.find(2);
        int condition = taskService.getAllScriptPaths(task).size();
        assertEquals("Size of tasks's all script paths is incorrect!", 3, condition);
    }

    @Test
    public void shouldGetAllScripts() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(2);
        String expected = "../type/automatic/script/path";
        String actual = taskService.getAllScripts(task).get("scriptName");
        assertEquals("Task's scripts doesn't match given plain text!", expected, actual);

        task = taskService.find(2);
        int condition = taskService.getAllScripts(task).size();
        assertEquals("Size of tasks's all scripts is incorrect!", 3, condition);
    }

    @Test
    public void shouldGetListOfPaths() throws Exception {
        TaskService taskService = new TaskService();

        Task task = taskService.find(2);
        String expected = "scriptName; secondScriptName; thirdScriptName";
        String actual = taskService.getListOfPaths(task);
        assertEquals("Task's scripts doesn't match given plain text!", expected, actual);
    }
}
