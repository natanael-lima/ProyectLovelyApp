package com.nl.lovely.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nl.lovely.dto.MessageDTO;
import com.nl.lovely.entity.Chat;
import com.nl.lovely.entity.Match;
import com.nl.lovely.entity.Message;
import com.nl.lovely.entity.User;
import com.nl.lovely.service.ChatService;
import com.nl.lovely.service.MatchService;
import com.nl.lovely.service.MessageService;
import com.nl.lovely.service.NotificationService;
import com.nl.lovely.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200") // Reemplaza esto con el dominio de tu frontend
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class WebSocketController {
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageService messageService;
	
	// API para chat en tiempo real usando websocket.
    @MessageMapping("/chat/{chatId}/{userId}")
    @SendTo("/topic/{chatId}")
    @Transactional
    public MessageDTO  chatMessage(@DestinationVariable Long chatId, @DestinationVariable Long userId, MessageDTO messageDTO) {
        // Obtener el chat correspondiente al chatId
        Chat chat = chatService.findChatById(chatId);
        // Asignar el chat al mensaje
        messageDTO.setChatId(chat.getId());
        // Asignar el ID del remitente al mensaje
        User sender = userService.findUserById(userId);
        messageDTO.setSenderId(sender.getId());
        messageDTO.setTimestamp(LocalDateTime.now());
        System.out.println("backend message content: "+messageDTO.getContent());
        // Guardar el mensaje en la base de datos
        messageService.saveMessage(messageDTO);
        // Devolver el mensaje para enviarlo al topic correspondiente al chatId
        return messageDTO;
    }

    // API para conseguir los mensajes de un chat.
    @GetMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessagesForChat(@PathVariable Long chatId) {
        //return messageService.findMessagesByChatId(chatId);
        try {
        	
        	List<MessageDTO> messages = messageService.findMessagesByChatId(chatId);
        	System.out.println("backend arraymjs: "+messages);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            // Manejo de errores
        	 e.printStackTrace();
        	 return ResponseEntity.badRequest().build();
        
        }
        
    }
    
    //************************** Metodo para obtener el id del user logueado.**************************
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
            	 System.out.println("id de user: "+user.getId());
                return user.getId();
               
            }
        }
        return null;
    }
    
    
    /*@MessageMapping("/chat/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage chat(@DestinationVariable String roomId, ChatMessage message) {
		System.out.println(message);
		return new ChatMessage(message.getMessage(), message.getUser());
	}*/

}
