import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import model.command.MemberRegisterCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.MemberManager;

public class Option2MemberRegisterCommandTest {

    private Path tempMembersFile;
    private MemberManager memberManager;

    @BeforeEach
    void setUp() throws Exception {
        resetMemberManagerSingleton();

        tempMembersFile = Files.createTempFile("members-option2-", ".json");
        String json = "{\n"
                + "  \"M001\": {\n"
                + "    \"id\": \"M001\",\n"
                + "    \"username\": \"alice123\",\n"
                + "    \"password\": \"password123\",\n"
                + "    \"name\": \"Alice\",\n"
                + "    \"phone\": \"91234567\",\n"
                + "    \"points\": 0,\n"
                + "    \"level\": \"Normal\",\n"
                + "    \"registerDate\": \"2026-01-01 10:00:00\"\n"
                + "  }\n"
                + "}";
        Files.writeString(tempMembersFile, json);

        memberManager = MemberManager.getInstance(tempMembersFile.toString());
    }

    @Test
    void option2_RegisterSuccessful_ShouldRegisterNewMember() throws Exception {
        String input = "newuser\nnewpass\nNew User\n12345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(memberManager.login("newuser", "newpass"));
        assertEquals("newuser", memberManager.getCurrentMember().getUsername());
        assertTrue(output.contains("--- Register (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Password:"));
        assertTrue(output.contains("Your Name:"));
        assertTrue(output.contains("Phone Number:"));
        assertTrue(output.contains("Registration successful. Please login."));
    }

    @Test
    void option2_RegisterDuplicateUsername_ShouldRejectAndExit() throws Exception {
        String input = "alice123\n-1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertFalse(memberManager.login("some-new-user", "some-pass"));
        assertTrue(memberManager.isExistUserName("alice123"));
        assertTrue(output.contains("--- Register (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Username already exist!"));
    }

    @Test
    void option2_RegisterMinusOneAtUsername_ShouldGoBack() throws Exception {
        String input = "-1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("--- Register (-1 to go back to previous step) ---"));
        assertTrue(output.contains("Username:"));
        assertFalse(output.contains("Password:"));
        assertFalse(memberManager.login("newuser", "newpass"));
    }

    @Test
    void option2_RegisterMinusOneAtPassword_ShouldGoBackToUsername() throws Exception {
        String input = "newuser\n-1\n-1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("Username:"));
        assertTrue(output.contains("Password:"));
        assertTrue(countOccurrences(output, "Username:") >= 2);
        assertFalse(memberManager.login("newuser", "newpass"));
    }

    @Test
    void option2_RegisterEmptyUsername_ShouldRejectThenAllowNextInput() throws Exception {
        String input = "\nnewuser2\nnewpass\nTester\n12345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("Username cannot be empty."));
        assertTrue(memberManager.login("newuser2", "newpass"));
    }

    @Test
    void option2_RegisterMinusOneAtName_ShouldGoBackToPassword() throws Exception {
        String input = "newnamecase\npass1\n-1\npass2\nUser Name\n12345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(countOccurrences(output, "Password:") >= 2);
        assertTrue(memberManager.login("newnamecase", "pass2"));
    }

    @Test
    void option2_RegisterMinusOneAtPhone_ShouldGoBackToName() throws Exception {
        String input = "newphonecase\npass1\nName1\n-1\nName2\n12345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(countOccurrences(output, "Your Name:") >= 2);
        assertTrue(memberManager.login("newphonecase", "pass1"));
    }

    @Test
    void option2_RegisterInvalidPhoneLengthAndNonDigit_ShouldRejectThenAcceptValidPhone() throws Exception {
        String input = "newphonevalid\npass1\nName\n123\n12ab5678\n12345678\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MemberRegisterCommand command = new MemberRegisterCommand(memberManager, scanner);

        String output = executeAndCaptureOutput(command);

        assertTrue(output.contains("Phone number must be exactly 8 digits."));
        assertTrue(output.contains("Phone number can only contain numbers."));
        assertTrue(memberManager.login("newphonevalid", "pass1"));
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }

    private String executeAndCaptureOutput(MemberRegisterCommand command) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            command.execute();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    private void resetMemberManagerSingleton() throws Exception {
        Field instanceField = MemberManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
