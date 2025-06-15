package com.example.womenapp; // Ensure this matches your project's package name

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ChatbotActivity handles the user interface and logic for the safety chatbot.
 * It displays chat messages, processes user input, and provides predefined responses.
 */
public class ChatbotActivity extends AppCompatActivity {

    // UI elements for the chat interface
    private RecyclerView chatRecyclerView;
    private EditText etChatMessage;
    private Button btnSendMessage;

    // Data structures for managing chat messages and chatbot responses
    private List<Message> messageList;
    private ChatAdapter chatAdapter;
    private Map<String, String> chatbotResponses; // Stores predefined questions and answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this activity to its corresponding XML layout file
        setContentView(R.layout.chatbotactivity); // IMPORTANT: Ensure your XML file is named 'chatbotactivity.xml'

        // Initialize UI elements by finding them from the layout
        // The previously commented line is now uncommented to properly initialize chatRecyclerView
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        etChatMessage = findViewById(R.id.messageInput);
        btnSendMessage = findViewById(R.id.sendButton);

        // Set up the RecyclerView to display chat messages
        messageList = new ArrayList<>(); // Initialize the list to store messages
        chatAdapter = new ChatAdapter(messageList); // Create an adapter for the RecyclerView
        // Set a vertical layout manager for the RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter); // Attach the adapter to the RecyclerView

        // Initialize the chatbot's knowledge base with predefined responses
        initializeChatbotResponses();

        // Greet the user and provide initial guidance when the chat activity starts
        addBotMessage("Hi! I'm your safety assistant. How can I help you?");
        addBotMessage("You can ask about: 'SOS message', 'Recording video', 'Emergency contacts', 'What to do if followed', or 'Emergency number'.");

        // Set a click listener for the send message button
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    /**
     * Initializes the HashMap with predefined chatbot questions (keywords) and their
     * corresponding answers. This acts as the chatbot's "brain" for keyword matching.
     */
    private void initializeChatbotResponses() {
        chatbotResponses = new HashMap<>();

        // --- Greetings and General Help ---
        chatbotResponses.put("hi", "Hello! How can I assist you with your safety today?");
        chatbotResponses.put("hello", "Hi there! What's on your mind regarding safety?");
        chatbotResponses.put("how are you", "I'm a safety assistant, designed to help you. How can I help you?");
        chatbotResponses.put("hey", "Hello! How can I assist you with your safety today?");
        chatbotResponses.put("what can you do", "I can provide guidance on using the SOS feature, video recording, managing emergency contacts, or general safety tips. Just ask!");
        chatbotResponses.put("how to use app", "This app helps you in emergencies. You can send SOS messages to contacts, and record video/audio. Ask me questions about these features!");
        chatbotResponses.put("help", "I can provide guidance on using the SOS feature, video recording, managing emergency contacts, or general safety tips. Just ask!");
        chatbotResponses.put("features", "This app offers SOS messaging to emergency contacts and video/audio recording for evidence. What would you like to know more about?");
        chatbotResponses.put("app functions", "This app offers SOS messaging to emergency contacts and video/audio recording for evidence. What would you like to know more about?");
        chatbotResponses.put("how", "Please tell me what you want to know 'how' to do. For example, 'how to send SOS' or 'how to record video'.");
        chatbotResponses.put("tell me how", "Please tell me what you want to know 'how' to do. For example, 'how to send SOS' or 'how to record video'.");


        // --- SOS Message Related ---
        // Responses here guide the user to the main activity's SOS functionality.
        chatbotResponses.put("sos message", "To send an SOS message, please go back to the main screen and add emergency numbers at the top, then tap the 'Send SOS' button. It will alert your contacts with your location (if enabled).");
        chatbotResponses.put("send sos", "To send an SOS message, please go back to the main screen and add emergency numbers at the top, then tap the 'Send SOS' button. It will alert your contacts with your location (if enabled).");
        chatbotResponses.put("sos", "To send an SOS message, please go back to the main screen and add emergency numbers at the top, then tap the 'Send SOS' button. It will alert your contacts with your location (if enabled).");
        chatbotResponses.put("emergency alert", "To send an SOS message, please go back to the main screen and add emergency numbers at the top, then tap the 'Send SOS' button. It will alert your contacts with your location (if enabled).");
        chatbotResponses.put("how to send sos", "Just tap the 'Send SOS' button on the main screen after adding your emergency contacts. Your location will be sent automatically if enabled.");
        chatbotResponses.put("sos button", "The 'Send SOS' button is located on the app's main screen. It sends an alert to your designated emergency contacts.");
        chatbotResponses.put("alert contacts", "To alert your contacts, you use the 'Send SOS' button on the main screen after setting up their numbers.");
        chatbotResponses.put("emergency call", "This app focuses on sending SOS messages and recording. For an emergency call, dial 112 (India's emergency number).");
        chatbotResponses.put("location sending", "When you send an SOS message from the main app feature, your location data will be included in the message to your emergency contacts, if your location permissions are granted.");


        // --- Recording Video/Audio Related ---
        // Responses here guide the user to the main activity's recording functionality.
        chatbotResponses.put("recording video", "To start recording, look for 'Start Recording' button on the app's main screen. It captures video and audio. To stop, tap 'Stop Recording'.");
        chatbotResponses.put("start recording", "To start recording, tap 'Start Recording' on the main screen.");
        chatbotResponses.put("stop recording", "To stop recording, tap 'Stop Recording' on the main screen. Your current video and audio will be saved to your phone's gallery.");
        chatbotResponses.put("record video", "You can record video using the 'Start Recording' button on the app's main screen.");
        chatbotResponses.put("record audio", "You can record audio using the 'Start Recording' button on the app's main screen, as it records both.");
        chatbotResponses.put("how to record", "Tap 'Start Recording' on the main screen to begin and 'Stop Recording' to save the video and audio to your gallery.");
        chatbotResponses.put("capture evidence", "The recording feature on the main screen allows you to capture video and audio evidence. Just tap 'Start Recording' and 'Stop Recording'.");
        chatbotResponses.put("save video", "All recorded videos are saved automatically to your phone's gallery under 'WomenAppVideos' folder.");
        chatbotResponses.put("camera recording", "The camera recording feature captures video and audio. Ensure camera and microphone permissions are granted for the app.");


        // --- Emergency Contacts Related ---
        chatbotResponses.put("emergency contacts", "You can set up to two emergency contact numbers directly on the app's main screen. These numbers will receive your SOS message.");
        chatbotResponses.put("add contact", "You can add up to two emergency contact numbers on the main screen. These numbers will receive your SOS message.");
        chatbotResponses.put("contact numbers", "You can set up to two emergency contact numbers on the main screen. These numbers will receive your SOS message.");
        chatbotResponses.put("manage contacts", "The emergency contact numbers can be entered and changed directly at the top of the app's main screen.");
        chatbotResponses.put("who receives sms", "The numbers you enter as Emergency Number 1 and Emergency Number 2 on the main screen will receive the SOS message.");


        // --- Safety Tips ---
        chatbotResponses.put("what to do if followed", "If you feel followed, try to enter a public place, like a store or restaurant. Call a trusted person, or simulate a call. If danger is imminent, use the app's SOS button.");
        chatbotResponses.put("self defense tips", "Always be aware of your surroundings. If confronted, yell loudly, make noise, and target vulnerable areas if you must defend yourself. Consider taking a self-defense class.");
        chatbotResponses.put("dangerous situation", "In a dangerous situation, prioritize your safety. If possible, call 112, use the app's SOS button, and record evidence. Move to a safe, public space if you can.");
        chatbotResponses.put("report incident", "If you've experienced an incident, please report it to the police immediately. The recordings from this app can serve as evidence.");
        chatbotResponses.put("safety tips", "Remember to always be aware of your surroundings. If you feel unsafe, trust your instincts. Public places, calling a trusted person, or using the app's SOS feature can help.");
        chatbotResponses.put("feeling unsafe", "If you feel unsafe, try to move to a well-lit, public area. If you can, call a friend or family member, or use the app's SOS feature.");
        chatbotResponses.put("i feel unsafe", "If you feel unsafe, try to move to a well-lit, public area. If you can, call a friend or family member, or use the app's SOS feature.");
        chatbotResponses.put("feel unsafe", "If you feel unsafe, try to move to a well-lit, public area. If you can, call a friend or family member, or use the app's SOS feature.");
        chatbotResponses.put("get away", "If you need to get away from a situation, try to draw attention to yourself, make noise, and run towards a public place or trusted individuals.");


        // --- Location/Police Related ---
        chatbotResponses.put("nearby police station", "I cannot directly find nearby police stations. Please use a map application like Google Maps and search for 'police station near me'. For immediate danger, call 112 (all-India emergency number).");
        chatbotResponses.put("police station", "I cannot directly find nearby police stations. Please use a map application like Google Maps and search for 'police station near me'. For immediate danger, call 112 (all-India emergency number).");
        chatbotResponses.put("find police", "I cannot directly find nearby police stations. Please use a map application like Google Maps and search for 'police station near me'. For immediate danger, call 112 (all-India emergency number).");
        chatbotResponses.put("emergency number", "112"); // Short response as requested
        chatbotResponses.put("what is emergency number", "112");
        chatbotResponses.put("call 112", "112 is the all-India emergency number. You can call it for police, fire, and ambulance services.");
        chatbotResponses.put("location tracking", "This app does not track your location continuously. It only shares your location when you send an SOS message, provided location permissions are enabled.");


        // --- Positive Affirmations / Thanks ---
        chatbotResponses.put("thank you", "You're most welcome! Stay safe.");
        chatbotResponses.put("thanks", "Glad I could help! Is there anything else about safety I can assist with?");
        chatbotResponses.put("good bot", "Thank you! I'm here to help keep you safe.");
        chatbotResponses.put("you're helpful", "Thank you! I'm glad I could assist you.");
        chatbotResponses.put("appreciate", "You're welcome! Stay safe.");
    }

    /**
     * Handles sending a user message, adding it to the chat, and getting a bot response.
     * This method is triggered when the user taps the "Send" button.
     */
    private void sendMessage() {
        String messageText = etChatMessage.getText().toString().trim();
        // Prevent sending empty messages
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the user's message to the chat display
        addMessage(new Message(messageText, true));

        // Get the appropriate response from the chatbot based on the user's message
        String botResponse = getChatbotResponse(messageText);
        // Add the chatbot's response to the chat display
        addBotMessage(botResponse);

        // Clear the input field after sending and scroll to the bottom of the chat
        etChatMessage.setText("");
        // Ensure RecyclerView scrolls to the last message
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    /**
     * Adds a new message (either from the user or the bot) to the message list
     * and updates the RecyclerView to display it.
     * @param message The Message object containing the text and sender type.
     */
    private void addMessage(Message message) {
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1); // Notify adapter that a new item was added
        // Ensure RecyclerView scrolls to the last message after adding
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    /**
     * Convenience method to add a message specifically from the chatbot.
     * @param text The text content of the chatbot's message.
     */
    private void addBotMessage(String text) {
        addMessage(new Message(text, false)); // 'false' indicates it's a bot message
    }

    /**
     * Determines the chatbot's response by checking if the user's message contains any
     * of the predefined keywords. It performs a case-insensitive search.
     * @param userMessage The input message provided by the user.
     * @return The corresponding chatbot response string, or a default fallback message
     * if no relevant keyword is found.
     */
    private String getChatbotResponse(String userMessage) {
        // Convert user message to lowercase for case-insensitive matching
        String lowerCaseMessage = userMessage.toLowerCase(Locale.getDefault());

        // Iterate through all predefined chatbot responses
        for (Map.Entry<String, String> entry : chatbotResponses.entrySet()) {
            // If the user's message contains a keyword, return its associated response
            if (lowerCaseMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        // If no matching keyword is found, return a default "I'm sorry" message
        return "I'm sorry, I couldn't find an answer to that. Please try rephrasing or ask about 'SOS message', 'Recording video', 'Emergency contacts', or 'Safety tips'.";
    }
}