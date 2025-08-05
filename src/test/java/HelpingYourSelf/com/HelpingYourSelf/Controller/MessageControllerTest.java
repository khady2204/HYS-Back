package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MessageResponse;
import HelpingYourSelf.com.HelpingYourSelf.Service.MessageService;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testSendMessage_Success() throws Exception {
        Mockito.when(messageService.sendMessage(any(), any(MessageRequest.class)))
                .thenReturn(new MessageResponse(1L, 1L, 2L, "Hello", null, "/media/image.png", "image", null));

        MockMultipartFile file = new MockMultipartFile("mediaFile", "image.png", MediaType.IMAGE_PNG_VALUE, "dummy".getBytes());

        mockMvc.perform(multipart("/api/messages")
                .file(file)
                .param("receiverId", "2")
                .param("content", "Hello")
                .param("mediaType", "image")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testSendVoiceMessage_Success() throws Exception {
        Mockito.when(messageService.sendMessage(any(), any(MessageRequest.class)))
                .thenReturn(new MessageResponse(1L, 1L, 2L, null, null, "/media/voice.ogg", "audio", 5));

        MockMultipartFile file = new MockMultipartFile("mediaFile", "voice.ogg", "audio/ogg", "dummy".getBytes());

        mockMvc.perform(multipart("/api/messages")
                .file(file)
                .param("receiverId", "2")
                .param("mediaType", "audio")
                .param("audioDuration", "5")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendMessage_Unauthorized() throws Exception {
        MockMultipartFile file = new MockMultipartFile("mediaFile", "image.png", MediaType.IMAGE_PNG_VALUE, "dummy".getBytes());

        mockMvc.perform(multipart("/api/messages")
                .file(file)
                .param("receiverId", "2")
                .param("content", "Hello")
                .param("mediaType", "image")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound()); // 302 redirect to login page
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetMessages_Success() throws Exception {
        Mockito.when(messageService.getMessagesBetweenUsers(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/messages/2")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMessages_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/messages/2")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound()); // 302 redirect to login page
    }
}
