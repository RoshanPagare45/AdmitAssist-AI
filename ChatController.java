package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.Course;
import com.college.admission_chatbot.entity.User;
import com.college.admission_chatbot.entity.ChatLog;
import com.college.admission_chatbot.repository.CourseRepository;
import com.college.admission_chatbot.repository.UserRepository;
import com.college.admission_chatbot.repository.ChatLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
public class ChatController {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatLogRepository chatLogRepository;

    // Placement data storage
    private final Map<String, Map<String, Integer>> placementData = new HashMap<>();
    private final Map<String, Map<String, Integer>> branchWisePlacement = new HashMap<>();
    
    // Company names for each branch and year
    private final Map<String, Map<String, List<String>>> companyData = new HashMap<>();

    public ChatController() {
        initializePlacementData();
        initializeCompanyData();
    }

    private void initializePlacementData() {
        // Year-wise placement totals
        Map<String, Integer> yearWiseData = new LinkedHashMap<>();
        yearWiseData.put("2020-21", 343);
        yearWiseData.put("2021-22", 388);
        yearWiseData.put("2022-23", 383);
        yearWiseData.put("2023-24", 257);
        yearWiseData.put("2024-25", 242);
        placementData.put("total", yearWiseData);

        // Branch-wise placement data
        Map<String, Integer> mechanical = new LinkedHashMap<>();
        mechanical.put("2020-21", 77);
        mechanical.put("2021-22", 86);
        mechanical.put("2022-23", 111);
        mechanical.put("2023-24", 29);
        mechanical.put("2024-25", 31);
        branchWisePlacement.put("mechanical engineering", mechanical);
        branchWisePlacement.put("mechanical", mechanical);

        Map<String, Integer> electrical = new LinkedHashMap<>();
        electrical.put("2020-21", 92);
        electrical.put("2021-22", 110);
        electrical.put("2022-23", 142);
        electrical.put("2023-24", 92);
        electrical.put("2024-25", 125);
        branchWisePlacement.put("electrical engineering", electrical);
        branchWisePlacement.put("electrical", electrical);

        Map<String, Integer> computer = new LinkedHashMap<>();
        computer.put("2020-21", 110);
        computer.put("2021-22", 116);
        computer.put("2022-23", 59);
        computer.put("2023-24", 67);
        computer.put("2024-25", 26);
        branchWisePlacement.put("computer engineering", computer);
        branchWisePlacement.put("computer", computer);
        branchWisePlacement.put("cse", computer);

        Map<String, Integer> etc = new LinkedHashMap<>();
        etc.put("2020-21", 44);
        etc.put("2021-22", 38);
        etc.put("2022-23", 50);
        etc.put("2023-24", 40);
        etc.put("2024-25", 36);
        branchWisePlacement.put("electronics engineering", etc);
        branchWisePlacement.put("electronics", etc);
        branchWisePlacement.put("e&tc", etc);
        branchWisePlacement.put("etc", etc);

        Map<String, Integer> civil = new LinkedHashMap<>();
        civil.put("2020-21", 13);
        civil.put("2021-22", 13);
        civil.put("2022-23", 3);
        civil.put("2023-24", 13);
        civil.put("2024-25", 3);
        branchWisePlacement.put("civil engineering", civil);
        branchWisePlacement.put("civil", civil);

        Map<String, Integer> mca = new LinkedHashMap<>();
        mca.put("2020-21", 7);
        mca.put("2021-22", 25);
        mca.put("2022-23", 18);
        mca.put("2023-24", 16);
        mca.put("2024-25", 21);
        branchWisePlacement.put("mca", mca);
        branchWisePlacement.put("master of computer applications", mca);
    }
    
    private void initializeCompanyData() {
        // MCA Companies
        Map<String, List<String>> mcaCompanies = new HashMap<>();
        
        mcaCompanies.put("2020-21", Arrays.asList(
            "Infosys (3 students)", 
            "TCS (2 students)", 
            "Accenture (1 student)", 
            "Wipro (1 student)"
        ));
        
        mcaCompanies.put("2021-22", Arrays.asList(
            "TCS (8 students)", 
            "Infosys (6 students)", 
            "Accenture (4 students)", 
            "Wipro (3 students)", 
            "Capgemini (2 students)", 
            "Cognizant (2 students)"
        ));
        
        mcaCompanies.put("2022-23", Arrays.asList(
            "Infosys (5 students)", 
            "TCS (4 students)", 
            "Accenture (3 students)", 
            "Wipro (2 students)", 
            "Capgemini (2 students)", 
            "Tech Mahindra (1 student)", 
            "L&T Infotech (1 student)"
        ));
        
        mcaCompanies.put("2023-24", Arrays.asList(
            "Infosys (4 students)", 
            "TCS (3 students)", 
            "Accenture (3 students)", 
            "Wipro (2 students)", 
            "Capgemini (2 students)", 
            "Cognizant (1 student)", 
            "L&T Infotech (1 student)"
        ));
        
        mcaCompanies.put("2024-25", Arrays.asList(
            "Infosys (5 students)", 
            "TCS (4 students)", 
            "Accenture (3 students)", 
            "Wipro (3 students)", 
            "Capgemini (2 students)", 
            "Cognizant (2 students)", 
            "Tech Mahindra (1 student)", 
            "L&T Infotech (1 student)"
        ));
        
        companyData.put("mca", mcaCompanies);
        companyData.put("master of computer applications", mcaCompanies);
        
        // M.E. (Mechanical) Companies
        Map<String, List<String>> meCompanies = new HashMap<>();
        meCompanies.put("2023-24", Arrays.asList(
            "L&T (8 students)", 
            "Tata Motors (6 students)", 
            "Mahindra (5 students)", 
            "Bajaj Auto (4 students)", 
            "Cummins (3 students)", 
            "Siemens (2 students)", 
            "Bosch (1 student)"
        ));
        
        meCompanies.put("2024-25", Arrays.asList(
            "L&T (9 students)", 
            "Tata Motors (7 students)", 
            "Mahindra (6 students)", 
            "Bajaj Auto (5 students)", 
            "Cummins (3 students)", 
            "Siemens (3 students)", 
            "Bosch (2 students)", 
            "Thermax (1 student)"
        ));
        
        companyData.put("mechanical engineering", meCompanies);
        companyData.put("mechanical", meCompanies);
        companyData.put("m.e.", meCompanies);
        companyData.put("me", meCompanies);
    }

    // ========== UPDATED CHAT METHOD - SAVES TO DATABASE ==========
 @PostMapping("/chat")
public ResponseEntity<String> chat(@RequestBody String message, 
                                    @RequestHeader(value = "X-Language", defaultValue = "en") String language,
                                    HttpSession session) {
    
    String originalMessage = message;
    message = message.toLowerCase().trim();
    message = message.replace(".", "").replace("?", "").replace("!", "");
    
    String response = getResponse(message, language);
    
    // Save chat to database
    User currentUser = null;
    Long userId = (Long) session.getAttribute("userId");
    
    if (userId != null) {
        currentUser = userRepository.findById(userId).orElse(null);
    }
    
    if (currentUser == null) {
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            currentUser = allUsers.get(allUsers.size() - 1);
        }
    }
    
    if (currentUser != null) {
        ChatLog chatLog = new ChatLog();
        chatLog.setUser(currentUser);
        chatLog.setUserMessage(originalMessage);
        chatLog.setBotResponse(response);
        chatLog.setCreatedAt(java.time.LocalDateTime.now());
        chatLogRepository.save(chatLog);
        System.out.println("Chat saved for user: " + currentUser.getEmail());
    }
    
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
            .body(response);
}
    
    private String getResponse(String message, String language) {
        
        // ================= LANGUAGE DETECTION =================
        String detectedLang = detectLanguageFromMessage(message);
        if (!language.equals("en") || !detectedLang.equals("en")) {
            language = detectedLang;
        }

        // ================= CHECK FAQS FIRST =================
        String faqResponse = handleFAQ(message, language);
        if (faqResponse != null) {
            return faqResponse;
        }

        // ================= MCA & M.E BOTH QUERY =================
        if ((message.contains("mca") && message.contains("me")) || 
            (message.contains("mca") && message.contains("m.e.")) ||
            message.contains("both") || message.contains("about mca and me")) {
            return getMCAAndMEDetails(language);
        }
        
        // ================= M.E. PLACEMENT QUERY =================
        if (message.contains("me placement") || message.contains("m.e. placement") || 
            (message.contains("me") && message.contains("placement"))) {
            return getMECoursePlacementResponse(message, language);
        }

        // ================= MCA PLACEMENT QUERY =================
        if (message.contains("mca placement") || (message.contains("mca") && message.contains("placement"))) {
            return getMCACoursePlacementResponse(message, language);
        }

        // ================= PLACEMENT RELATED QUERIES =================
        if (isPlacementQuery(message)) {
            return handlePlacementQuery(message, language);
        }

        // ================= GREETING =================
        if (message.contains("hi") || message.contains("hello") || 
            message.contains("नमस्ते") || message.contains("नमस्कार")) {
            return getGreetingResponse(language);
        }

        // ================= COLLEGE INFO =================
        if (message.contains("college name") || message.contains("कॉलेज का नाम") || 
            message.contains("कॉलेजचे नाव")) {
            return getCollegeNameResponse(language);
        }

        if (message.contains("address") || message.contains("पता") || 
            message.contains("पत्ता")) {
            return getAddressResponse(language);
        }

        if (message.contains("affiliated") || message.contains("संबद्ध") || 
            message.contains("संलग्न")) {
            return getAffiliationResponse(language);
        }

        // ================= COURSE DETECTION =================
        String courseName = null;

        if (message.contains("mca") || message.contains("एमसीए")) {
            courseName = "MCA";
        } else if (message.contains("me") || message.contains("m.e.") || 
                   message.contains("एमई") || message.contains("एम.ई")) {
            courseName = "M.E.";
        }

        if (courseName != null) {
            List<Course> list = courseRepository.findByCourseName(courseName);
            if (!list.isEmpty()) {
                Course c = list.get(0);
                return getProfessionalCourseResponse(c, language);
            }
        }

        return getDefaultHelpResponse(language);
    }
    
    // ================= NEW HELPER METHOD FOR COURSE LIST =================
    private String getCourseListResponse(String language) {
        if (language.equals("hi")) {
            return """
                उपलब्ध पाठ्यक्रम:
                
                --------------------------------------------------
                
                स्नातकोत्तर कार्यक्रम (पीजी):
                
                1. MCA (मास्टर ऑफ कंप्यूटर एप्लीकेशन्स)
                   • अवधि: 2 वर्ष
                   • सीटें: 60
                   • फीस: ₹90,000/वर्ष
                
                2. M.E. (मास्टर ऑफ इंजीनियरिंग)
                   • अवधि: 2 वर्ष
                   • विशेषज्ञता: 4
                   • फीस: ₹1,00,000/वर्ष
                   
                   विशेषज्ञता:
                   - कंप्यूटर इंजीनियरिंग (12 सीटें)
                   - मैकेनिकल इंजीनियरिंग (12 सीटें)
                   - इलेक्ट्रिकल इंजीनियरिंग (12 सीटें)
                   - सिविल इंजीनियरिंग (12 सीटें)
                """;
        } else if (language.equals("mr")) {
            return """
                उपलब्ध अभ्यासक्रम:
                
                --------------------------------------------------
                
                पदव्युत्तर कार्यक्रम (पीजी):
                
                1. MCA (मास्टर ऑफ कॉम्प्युटर ऍप्लिकेशन्स)
                   • कालावधी: 2 वर्षे
                   • जागा: 60
                   • फी: ₹90,000/वर्ष
                
                2. M.E. (मास्टर ऑफ इंजिनिअरिंग)
                   • कालावधी: 2 वर्षे
                   • विशेषज्ञता: 4
                   • फी: ₹1,00,000/वर्ष
                   
                   विशेषज्ञता:
                   - कॉम्प्युटर इंजिनिअरिंग (12 जागा)
                   - मेकॅनिकल इंजिनिअरिंग (12 जागा)
                   - इलेक्ट्रिकल इंजिनिअरिंग (12 जागा)
                   - सिव्हिल इंजिनिअरिंग (12 जागा)
                """;
        } else {
            return """
                Available Courses:
                
                --------------------------------------------------
                
                Postgraduate Programs (PG):
                
                1. MCA (Master of Computer Applications)
                   • Duration: 2 Years
                   • Intake: 60 seats
                   • Fee: ₹90,000/year
                
                2. M.E. (Master of Engineering)
                   • Duration: 2 Years
                   • Specializations: 4
                   • Fee: ₹1,00,000/year
                   
                   Specializations:
                   - Computer Engineering (12 seats)
                   - Mechanical Engineering (12 seats)
                   - Electrical Engineering (12 seats)
                   - Civil Engineering (12 seats)
                """;
        }
    }
    
    // ================= RESPONSE FOR MCA & M.E BOTH - HINDI/MARATHI VERSION =================
    private String getMCAAndMEDetails(String language) {
        if (language.equals("hi")) {
            return """
                MCA और M.E. कार्यक्रम का विवरण
                
                --------------------------------------------------
                
                MCA (Master of Computer Applications)
                अवधि: 2 वर्ष
                सीटें: 120
                ट्यूशन फीस: ₹85,000 प्रति वर्ष
                पात्रता: गणित के साथ स्नातक
                प्रवेश परीक्षा: MAH-MCA CET
                औसत पैकेज: 6.5 LPA
                उच्चतम पैकेज: 12 LPA
                
                M.E. (Master of Engineering)
                अवधि: 2 वर्ष
                विशेषज्ञता: मैकेनिकल, इलेक्ट्रिकल, सिविल, कंप्यूटर
                ट्यूशन फीस: ₹95,000 प्रति वर्ष
                पात्रता: संबंधित शाखा में B.E./B.Tech
                प्रवेश परीक्षा: GATE / MAH-M.E. CET
                औसत पैकेज: 7.1 LPA
                उच्चतम पैकेज: 15 LPA
                
                --------------------------------------------------
                विशेष जानकारी के लिए पूछें:
                MCA प्लेसमेंट 2024
                M.E. प्रवेश प्रक्रिया
                कंप्यूटर इंजीनियरिंग कट-ऑफ
                """;
        } else if (language.equals("mr")) {
            return """
                MCA आणि M.E. अभ्यासक्रमाची माहिती
                
                --------------------------------------------------
                
                MCA (Master of Computer Applications)
                कालावधी: 2 वर्षे
                जागा: 120
                ट्यूशन फी: ₹85,000 प्रति वर्ष
                पात्रता: गणितासह पदवीधर
                प्रवेश परीक्षा: MAH-MCA CET
                सरासरी पॅकेज: 6.5 LPA
                सर्वोच्च पॅकेज: 12 LPA
                
                M.E. (Master of Engineering)
                कालावधी: 2 वर्षे
                विशेषज्ञता: मेकॅनिकल, इलेक्ट्रिकल, सिव्हिल, कॉम्प्युटर
                ट्यूशन फी: ₹95,000 प्रति वर्ष
                पात्रता: संबंधित शाखेत B.E./B.Tech
                प्रवेश परीक्षा: GATE / MAH-M.E. CET
                सरासरी पॅकेज: 7.1 LPA
                सर्वोच्च पॅकेज: 15 LPA
                
                --------------------------------------------------
                विशेष माहितीसाठी विचारा:
                MCA प्लेसमेंट 2024
                M.E. प्रवेश प्रक्रिया
                कॉम्प्युटर इंजिनिअरिंग कट-ऑफ
                """;
        } else {
            return getMCAAndMEDetailsEnglish();
        }
    }
    
    private String getMCAAndMEDetailsEnglish() {
        return """
            Program Overview: MCA and M.E.
            
            --------------------------------------------------
            
            MCA (Master of Computer Applications)
            Duration: 2 Years
            Intake: 120 Seats
            Tuition Fee: ₹85,000 per year
            Eligibility: Graduate with Mathematics
            Entrance Exam: MAH-MCA CET
            Average Package: 6.5 LPA
            Highest Package: 12 LPA
            
            M.E. (Master of Engineering)
            Duration: 2 Years
            Specializations: Mechanical, Electrical, Civil, Computer
            Tuition Fee: ₹95,000 per year
            Eligibility: B.E./B.Tech in relevant branch
            Entrance Exam: GATE / MAH-M.E. CET
            Average Package: 7.1 LPA
            Highest Package: 15 LPA
            
            --------------------------------------------------
            For specific details, ask:
            MCA placement 2024
            M.E. admission process
            Computer engineering cut-off
            """;
    }
    
    // ================= PROFESSIONAL MCA PLACEMENT RESPONSE - HINDI/MARATHI =================
    private String getMCACoursePlacementResponse(String message, String language) {
        Map<String, Integer> placementNumbers = branchWisePlacement.get("mca");
        Map<String, List<String>> companies = companyData.get("mca");
        
        String year = "2024-25";
        if (message.contains("2023")) year = "2023-24";
        else if (message.contains("2022")) year = "2022-23";
        else if (message.contains("2021")) year = "2021-22";
        else if (message.contains("2020")) year = "2020-21";
        
        Integer count = placementNumbers.get(year);
        List<String> yearCompanies = companies.get(year);
        
        StringBuilder companyText = new StringBuilder();
        for (int i = 0; i < yearCompanies.size(); i++) {
            companyText.append("  ").append(i+1).append(". ").append(yearCompanies.get(i));
            if (i < yearCompanies.size() - 1) companyText.append("\n");
        }
        
        String note = year.equals("2024-25") ? "\n\n  *चालू शैक्षणिक वर्षासाठी प्लेसमेंट सुरू आहेत" : "";
        String noteHi = year.equals("2024-25") ? "\n\n  *चालू शैक्षणिक वर्ष के लिए प्लेसमेंट जारी हैं" : "";
        
        if (language.equals("hi")) {
            return String.format("""
                MCA प्लेसमेंट रिपोर्ट %s
                
                --------------------------------------------------
                
                कुल छात्र प्लेस हुए: %d
                
                भर्ती करने वाली कंपनियां:
                %s
                
                पैकेज आंकड़े:
                  रेंज: 4.0 - 12.0 LPA
                  औसत: 6.5 LPA
                  उच्चतम: 12.0 LPA
                
                --------------------------------------------------
                शीर्ष भर्तीकर्ता: Infosys, TCS, Accenture, Wipro, Capgemini%s
                """, year, count, companyText.toString(), noteHi);
        } else if (language.equals("mr")) {
            return String.format("""
                MCA प्लेसमेंट अहवाल %s
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: %d
                
                भर्ती कंपन्या:
                %s
                
                पॅकेज आकडेवारी:
                  रेंज: 4.0 - 12.0 LPA
                  सरासरी: 6.5 LPA
                  सर्वोच्च: 12.0 LPA
                
                --------------------------------------------------
                शीर्ष भर्तीकर्ता: Infosys, TCS, Accenture, Wipro, Capgemini%s
                """, year, count, companyText.toString(), note);
        } else {
            return String.format("""
                MCA Placement Report %s
                
                --------------------------------------------------
                
                Total Students Placed: %d
                
                Recruiting Companies:
                %s
                
                Package Statistics:
                  Range: 4.0 - 12.0 LPA
                  Average: 6.5 LPA
                  Highest: 12.0 LPA
                
                --------------------------------------------------
                Top Recruiters: Infosys, TCS, Accenture, Wipro, Capgemini
                """, year, count, companyText.toString());
        }
    }
    
    // ================= PROFESSIONAL M.E. PLACEMENT RESPONSE - HINDI/MARATHI =================
    private String getMECoursePlacementResponse(String message, String language) {
        Map<String, Integer> placementNumbers = branchWisePlacement.get("mechanical engineering");
        Map<String, List<String>> companies = companyData.get("mechanical engineering");
        
        String year = "2024-25";
        if (message.contains("2023")) year = "2023-24";
        
        Integer count = placementNumbers.get(year);
        List<String> yearCompanies = companies.get(year);
        
        StringBuilder companyText = new StringBuilder();
        for (int i = 0; i < yearCompanies.size(); i++) {
            companyText.append("  ").append(i+1).append(". ").append(yearCompanies.get(i));
            if (i < yearCompanies.size() - 1) companyText.append("\n");
        }
        
        String note = year.equals("2024-25") ? "\n\n  *चालू शैक्षणिक वर्षासाठी प्लेसमेंट सुरू आहेत" : "";
        String noteHi = year.equals("2024-25") ? "\n\n  *चालू शैक्षणिक वर्ष के लिए प्लेसमेंट जारी हैं" : "";
        
        if (language.equals("hi")) {
            return String.format("""
                M.E. (मैकेनिकल) प्लेसमेंट रिपोर्ट %s
                
                --------------------------------------------------
                
                कुल छात्र प्लेस हुए: %d
                
                भर्ती करने वाली कंपनियां:
                %s
                
                पैकेज आंकड़े:
                  रेंज: 5.0 - 15.0 LPA
                  औसत: 7.5 LPA
                  उच्चतम: 15.0 LPA
                
                --------------------------------------------------
                शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins%s
                
                अन्य विशेषज्ञता के लिए पूछें:
                इलेक्ट्रिकल इंजीनियरिंग प्लेसमेंट
                सिविल इंजीनियरिंग प्लेसमेंट
                कंप्यूटर इंजीनियरिंग प्लेसमेंट
                """, year, count, companyText.toString(), noteHi);
        } else if (language.equals("mr")) {
            return String.format("""
                M.E. (मेकॅनिकल) प्लेसमेंट अहवाल %s
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: %d
                
                भर्ती कंपन्या:
                %s
                
                पॅकेज आकडेवारी:
                  रेंज: 5.0 - 15.0 LPA
                  सरासरी: 7.5 LPA
                  सर्वोच्च: 15.0 LPA
                
                --------------------------------------------------
                शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins%s
                
                इतर विशेषज्ञतेसाठी विचारा:
                इलेक्ट्रिकल इंजिनिअरिंग प्लेसमेंट
                सिव्हिल इंजिनिअरिंग प्लेसमेंट
                कॉम्प्युटर इंजिनिअरिंग प्लेसमेंट
                """, year, count, companyText.toString(), note);
        } else {
            return String.format("""
                M.E. (Mechanical) Placement Report %s
                
                --------------------------------------------------
                
                Total Students Placed: %d
                
                Recruiting Companies:
                %s
                
                Package Statistics:
                  Range: 5.0 - 15.0 LPA
                  Average: 7.5 LPA
                  Highest: 15.0 LPA
                
                --------------------------------------------------
                Top Recruiters: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins%s
                
                For other specializations, ask:
                Electrical engineering placement
                Civil engineering placement
                Computer engineering placement
                """, year, count, companyText.toString(), "");
        }
    }
    
    // ================= PROFESSIONAL COURSE RESPONSE - HINDI/MARATHI =================
    private String getProfessionalCourseResponse(Course c, String language) {
        String courseName = c.getCourseName();
        
        if (courseName.equals("MCA")) {
            if (language.equals("hi")) {
                return String.format("""
                    MCA कार्यक्रम विवरण
                    
                    --------------------------------------------------
                    
                    अवधि: %s
                    सीट क्षमता: %d सीटें
                    ट्यूशन फीस: ₹%.0f प्रति वर्ष
                    
                    पात्रता मानदंड:
                    %s
                    
                    प्रवेश परीक्षा:
                    %s
                    
                    प्लेसमेंट हाइलाइट्स (2024-25):
                    कुल प्लेस: 21 छात्र
                    औसत पैकेज: 6.5 LPA
                    उच्चतम पैकेज: 12 LPA
                    
                    शीर्ष भर्तीकर्ता:
                    Infosys, TCS, Accenture, Wipro, Capgemini
                    
                    --------------------------------------------------
                    विस्तृत प्लेसमेंट डेटा के लिए:
                    MCA प्लेसमेंट 2024
                    MCA प्लेसमेंट इतिहास
                    """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
            } else if (language.equals("mr")) {
                return String.format("""
                    MCA अभ्यासक्रम माहिती
                    
                    --------------------------------------------------
                    
                    कालावधी: %s
                    जागा: %d
                    ट्यूशन फी: ₹%.0f प्रति वर्ष
                    
                    पात्रता निकष:
                    %s
                    
                    प्रवेश परीक्षा:
                    %s
                    
                    प्लेसमेंट हायलाइट्स (2024-25):
                    एकूण प्लेस: 21 विद्यार्थी
                    सरासरी पॅकेज: 6.5 LPA
                    सर्वोच्च पॅकेज: 12 LPA
                    
                    शीर्ष भर्तीकर्ता:
                    Infosys, TCS, Accenture, Wipro, Capgemini
                    
                    --------------------------------------------------
                    तपशीलवार प्लेसमेंट माहितीसाठी:
                    MCA प्लेसमेंट 2024
                    MCA प्लेसमेंट इतिहास
                    """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
            } else {
                return getEnglishCourseResponse(c);
            }
        } else {
            if (language.equals("hi")) {
                return String.format("""
                    M.E. कार्यक्रम विवरण
                    
                    --------------------------------------------------
                    
                    अवधि: %s
                    सीट क्षमता: %d सीटें
                    ट्यूशन फीस: ₹%.0f प्रति वर्ष
                    
                    पात्रता मानदंड:
                    %s
                    
                    प्रवेश परीक्षा:
                    %s
                    
                    प्लेसमेंट हाइलाइट्स (2024-25):
                    कुल प्लेस (सभी शाखाएं): 242 छात्र
                    औसत पैकेज: 7.1 LPA
                    उच्चतम पैकेज: 15 LPA
                    
                    शीर्ष भर्तीकर्ता:
                    L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins, Siemens
                    
                    --------------------------------------------------
                    शाखा-विशेष विवरण:
                    मैकेनिकल इंजीनियरिंग प्लेसमेंट
                    इलेक्ट्रिकल इंजीनियरिंग प्लेसमेंट
                    कंप्यूटर इंजीनियरिंग प्लेसमेंट
                    """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
            } else if (language.equals("mr")) {
                return String.format("""
                    M.E. अभ्यासक्रम माहिती
                    
                    --------------------------------------------------
                    
                    कालावधी: %s
                    जागा: %d
                    ट्यूशन फी: ₹%.0f प्रति वर्ष
                    
                    पात्रता निकष:
                    %s
                    
                    प्रवेश परीक्षा:
                    %s
                    
                    प्लेसमेंट हायलाइट्स (2024-25):
                    एकूण प्लेस (सर्व शाखा): 242 विद्यार्थी
                    सरासरी पॅकेज: 7.1 LPA
                    सर्वोच्च पॅकेज: 15 LPA
                    
                    शीर्ष भर्तीकर्ता:
                    L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins, Siemens
                    
                    --------------------------------------------------
                    शाखा-विशिष्ट माहिती:
                    मेकॅनिकल इंजिनिअरिंग प्लेसमेंट
                    इलेक्ट्रिकल इंजिनिअरिंग प्लेसमेंट
                    कॉम्प्युटर इंजिनिअरिंग प्लेसमेंट
                    """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
            } else {
                return getEnglishCourseResponse(c);
            }
        }
    }
    
    private String getEnglishCourseResponse(Course c) {
        String courseName = c.getCourseName();
        if (courseName.equals("MCA")) {
            return String.format("""
                MCA Program Details
                
                --------------------------------------------------
                
                Duration: %s
                Intake Capacity: %d Seats
                Tuition Fee: ₹%.0f per year
                
                Eligibility Criteria:
                %s
                
                Entrance Examination:
                %s
                
                Placement Highlights (2024-25):
                Total Placed: 21 students
                Average Package: 6.5 LPA
                Highest Package: 12 LPA
                
                Top Recruiters:
                Infosys, TCS, Accenture, Wipro, Capgemini
                
                --------------------------------------------------
                For detailed placement data:
                MCA placement 2024
                MCA placement history
                """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
        } else {
            return String.format("""
                M.E. Program Details
                
                --------------------------------------------------
                
                Duration: %s
                Intake Capacity: %d Seats
                Tuition Fee: ₹%.0f per year
                
                Eligibility Criteria:
                %s
                
                Entrance Examination:
                %s
                
                Placement Highlights (2024-25):
                Total Placed (All branches): 242 students
                Average Package: 7.1 LPA
                Highest Package: 15 LPA
                
                Top Recruiters:
                L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins, Siemens
                
                --------------------------------------------------
                Branch-specific details:
                Mechanical engineering placement
                Electrical engineering placement
                Computer engineering placement
                """, c.getDuration(), c.getIntake(), c.getFees(), c.getEligibility(), c.getEntranceExam());
        }
    }
    
    // ================= OTHER HELPER METHODS =================
    
    private boolean isPlacementQuery(String message) {
        String[] keywords = {"placement", "placements", "placed", "job", "package", "salary", "lpa"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) return true;
        }
        return false;
    }
    
    private String handlePlacementQuery(String message, String language) {
        if (message.contains("mca")) return getMCACoursePlacementResponse(message, language);
        if (message.contains("mechanical")) return getMECoursePlacementResponse(message, language);
        return getDefaultPlacementResponse(language);
    }
    
    private String getDefaultPlacementResponse(String language) {
        if (language.equals("hi")) {
            return """
                प्लेसमेंट सारांश 2024-25
                
                --------------------------------------------------
                
                कुल आंकड़े:
                कुल छात्र प्लेस: 242
                उच्चतम पैकेज: 15 LPA
                औसत पैकेज: 7.1 LPA
                
                शीर्ष भर्तीकर्ता:
                Infosys, TCS, Accenture, L&T, Tata Motors, Mahindra, Microsoft, Wipro, Capgemini
                
                शाखा-वार विवरण:
                MCA: 21 छात्र प्लेस
                कंप्यूटर इंजीनियरिंग: 26 छात्र
                मैकेनिकल इंजीनियरिंग: 31 छात्र
                इलेक्ट्रिकल इंजीनियरिंग: 125 छात्र
                इलेक्ट्रॉनिक्स इंजीनियरिंग: 36 छात्र
                सिविल इंजीनियरिंग: 3 छात्र
                
                --------------------------------------------------
                विशेष जानकारी के लिए:
                MCA प्लेसमेंट 2024
                मैकेनिकल इंजीनियरिंग प्लेसमेंट
                कंप्यूटर इंजीनियरिंग प्लेसमेंट 2023
                """;
        } else if (language.equals("mr")) {
            return """
                प्लेसमेंट सारांश 2024-25
                
                --------------------------------------------------
                
                एकूण आकडेवारी:
                एकूण विद्यार्थी प्लेस: 242
                सर्वोच्च पॅकेज: 15 LPA
                सरासरी पॅकेज: 7.1 LPA
                
                शीर्ष भर्तीकर्ता:
                Infosys, TCS, Accenture, L&T, Tata Motors, Mahindra, Microsoft, Wipro, Capgemini
                
                शाखा-निहाय तपशील:
                MCA: 21 विद्यार्थी
                कॉम्प्युटर इंजिनिअरिंग: 26 विद्यार्थी
                मेकॅनिकल इंजिनिअरिंग: 31 विद्यार्थी
                इलेक्ट्रिकल इंजिनिअरिंग: 125 विद्यार्थी
                इलेक्ट्रॉनिक्स इंजिनिअरिंग: 36 विद्यार्थी
                सिव्हिल इंजिनिअरिंग: 3 विद्यार्थी
                
                --------------------------------------------------
                विशेष माहितीसाठी:
                MCA प्लेसमेंट 2024
                मेकॅनिकल इंजिनिअरिंग प्लेसमेंट
                कॉम्प्युटर इंजिनिअरिंग प्लेसमेंट 2023
                """;
        } else {
            return """
                Placement Summary 2024-25
                
                --------------------------------------------------
                
                Overall Statistics:
                Total Students Placed: 242
                Highest Package: 15 LPA
                Average Package: 7.1 LPA
                
                Top Recruiters:
                Infosys, TCS, Accenture, L&T, Tata Motors, Mahindra, Microsoft, Wipro, Capgemini
                
                Branch-wise Details:
                MCA: 21 students placed
                Computer Engineering: 26 students
                Mechanical Engineering: 31 students
                Electrical Engineering: 125 students
                Electronics Engineering: 36 students
                Civil Engineering: 3 students
                
                --------------------------------------------------
                For specific information:
                MCA placement 2024
                Mechanical engineering placement
                Computer engineering placement 2023
                """;
        }
    }
    
    private String getGreetingResponse(String language) {
        if (language.equals("hi")) {
            return """
                पीजी प्रवेश पोर्टल में आपका स्वागत है
                
                --------------------------------------------------
                
                मैं आपकी सहायता कर सकता हूं:
                
                कार्यक्रम की जानकारी:
                MCA (Master of Computer Applications)
                M.E. (Master of Engineering)
                
                प्रवेश विवरण:
                पात्रता मानदंड
                प्रवेश परीक्षा
                फीस संरचना
                प्रवेश प्रक्रिया
                
                प्लेसमेंट आंकड़े:
                साल-दर-साल प्लेसमेंट डेटा
                कंपनी के नाम
                पैकेज विवरण
                
                --------------------------------------------------
                आज मैं आपकी कैसे सहायता कर सकता हूं?
                """;
        } else if (language.equals("mr")) {
            return """
                पीजी प्रवेश पोर्टल मध्ये आपले स्वागत आहे
                
                --------------------------------------------------
                
                मी तुम्हाला मदत करू शकतो:
                
                अभ्यासक्रम माहिती:
                MCA (Master of Computer Applications)
                M.E. (Master of Engineering)
                
                प्रवेश तपशील:
                पात्रता निकष
                प्रवेश परीक्षा
                फी रचना
                प्रवेश प्रक्रिया
                
                प्लेसमेंट आकडेवारी:
                वर्षनिहाय प्लेसमेंट माहिती
                कंपनीची नावे
                पॅकेज तपशील
                
                --------------------------------------------------
                आज मी तुम्हाला कशी मदत करू शकतो?
                """;
        } else {
            return """
                Welcome to PG Admission Portal
                
                --------------------------------------------------
                
                I can assist you with:
                
                Program Information:
                MCA (Master of Computer Applications)
                M.E. (Master of Engineering)
                
                Admission Details:
                Eligibility criteria
                Entrance examinations
                Fee structure
                Admission process
                
                Placement Statistics:
                Year-wise placement data
                Company names
                Package details
                
                --------------------------------------------------
                How may I help you today?
                """;
        }
    }
    
    private String getCollegeNameResponse(String language) {
        if (language.equals("hi")) {
            return "Gokhale Education Society's R. H. Sapat College of Engineering, Management Studies and Research, Nashik.";
        } else if (language.equals("mr")) {
            return "Gokhale Education Society's R. H. Sapat College of Engineering, Management Studies and Research, Nashik.";
        } else {
            return "Gokhale Education Society's R. H. Sapat College of Engineering, Management Studies and Research, Nashik.";
        }
    }
    
    private String getAddressResponse(String language) {
        if (language.equals("hi")) {
            return "पता: Vidya Nagar, College Road, Nashik - 422005.";
        } else if (language.equals("mr")) {
            return "पत्ता: Vidya Nagar, College Road, Nashik - 422005.";
        } else {
            return "Address: Vidya Nagar, College Road, Nashik - 422005.";
        }
    }
    
    private String getAffiliationResponse(String language) {
        if (language.equals("hi")) {
            return "कॉलेज Savitribai Phule Pune University से संबद्ध है और AICTE द्वारा अनुमोदित है।";
        } else if (language.equals("mr")) {
            return "कॉलेज Savitribai Phule Pune University शी संलग्न आहे आणि AICTE द्वारे मान्यताप्राप्त आहे.";
        } else {
            return "The college is affiliated to Savitribai Phule Pune University and approved by AICTE.";
        }
    }
    
    private String getDefaultHelpResponse(String language) {
        if (language.equals("hi")) {
            return """
                मैं आपकी मदद कर सकता हूं:
                
                MCA / M.E. कार्यक्रम विवरण
                प्लेसमेंट आंकड़े
                प्रवेश प्रक्रिया
                फीस संरचना
                पात्रता मानदंड
                
                कृपया कोई विशेष प्रश्न पूछें।
                """;
        } else if (language.equals("mr")) {
            return """
                मी तुम्हाला मदत करू शकतो:
                
                MCA / M.E. अभ्यासक्रम माहिती
                प्लेसमेंट आकडेवारी
                प्रवेश प्रक्रिया
                फी रचना
                पात्रता निकष
                
                कृपया विशिष्ट प्रश्न विचारा।
                """;
        } else {
            return """
                I can help you with:
                
                MCA / M.E. program details
                Placement statistics
                Admission process
                Fee structure
                Eligibility criteria
                
                Please ask a specific question.
                """;
        }
    }
    
    private String detectLanguageFromMessage(String message) {
        if (message.matches(".*[\\u0900-\\u097F].*")) {
            String[] marathiWords = {"आहे", "आहेत", "का", "चा", "ची", "चे", "ला", "साठी", "करण्यासाठी", "काय", "सांगा"};
            for (String word : marathiWords) {
                if (message.contains(word)) return "mr";
            }
            return "hi";
        }
        return "en";
    }

    // ==================== FAQ METHODS ====================
    
    private String handleFAQ(String message, String language) {
        
        // ==================== HINDI BUTTON TEXT DETECTION ====================
        if (message.contains("mca फीस") || (message.contains("mca") && message.contains("फीस"))) {
            return getMCAFee(language);
        }
        
        if (message.contains("me फीस") || (message.contains("me") && message.contains("फीस"))) {
            return getMEFee(language);
        }
        
        if (message.contains("प्रवेश तिथियाँ") || (message.contains("प्रवेश") && message.contains("तिथियाँ"))) {
            return getAdmissionDates(language);
        }
        
        if (message.contains("फीस संरचना")) {
            return (message.contains("mca") || message.contains("एमसीए")) ? getMCAFee(language) : getMEFee(language);
        }
        
        if (message.contains("कोर्स")) {
            return getCourseListResponse(language);
        }
        
        if (message.contains("प्लेसमेंट")) {
            if (message.contains("mca") || message.contains("एमसीए")) return getMCAPlacement(language);
            if (message.contains("me") || message.contains("एमई")) return getMEPlacement(language);
            return getDefaultPlacementResponse(language);
        }
        
        if (message.contains("छात्रवृत्ति")) {
            return getScholarshipInfo(language);
        }
        
        if (message.contains("हॉस्टल")) {
            return getHostelInfo(language);
        }
        
        // ==================== MARATHI BUTTON TEXT DETECTION ====================
        if (message.contains("mca फी") || (message.contains("mca") && message.contains("फी"))) {
            return getMCAFee(language);
        }
        
        if (message.contains("me फी") || (message.contains("me") && message.contains("फी"))) {
            return getMEFee(language);
        }
        
        if (message.contains("प्रवेश तारखा") || (message.contains("प्रवेश") && message.contains("तारखा"))) {
            return getAdmissionDates(language);
        }
        
        if (message.contains("फी रचना")) {
            return (message.contains("mca") || message.contains("एमसीए")) ? getMCAFee(language) : getMEFee(language);
        }
        
        if (message.contains("अभ्यासक्रम")) {
            return getCourseListResponse(language);
        }
        
        if (message.contains("शिष्यवृत्ती")) {
            return getScholarshipInfo(language);
        }
        
        if (message.contains("हॉस्टेल")) {
            return getHostelInfo(language);
        }
        
        // ==================== COURSE OVERVIEW FAQS ====================
        if (message.contains("what is mca") || message.contains("about mca") || 
            message.contains("mca kya hai") || (message.contains("mca") && message.contains("explain"))) {
            return getMCAOverview(language);
        }
        
        if (message.contains("what is me") || message.contains("what is m.e.") || 
            message.contains("about me") || message.contains("about m.e.") || 
            message.contains("me kya hai")) {
            return getMEOverview(language);
        }
        
        if ((message.contains("difference") || message.contains("diff") || message.contains("antar") || message.contains("fark")) && 
            (message.contains("mca") && (message.contains("me") || message.contains("m.e.")))) {
            return getMCAMEDifference(language);
        }
        
        // ==================== ADMISSION FAQS ====================
        if ((message.contains("admission") || message.contains("apply") || message.contains("pravesh")) && 
            message.contains("process") && message.contains("mca")) {
            return getMCAAdmissionProcess(language);
        }
        
        if ((message.contains("admission") || message.contains("apply") || message.contains("pravesh")) && 
            message.contains("process") && (message.contains("me") || message.contains("m.e."))) {
            return getMEAdmissionProcess(language);
        }
        
        if (message.contains("admission start") || message.contains("admission date") || 
            message.contains("when admission") || message.contains("pravesh kab hoga")) {
            return getAdmissionDates(language);
        }
        
        if (message.contains("last date") || message.contains("deadline") || message.contains("akhri date")) {
            return getLastDate(language);
        }
        
        // ==================== ELIGIBILITY FAQS ====================
        if ((message.contains("eligibility") || message.contains("patrata")) && message.contains("mca")) {
            return getMCAEligibility(language);
        }
        
        if ((message.contains("eligibility") || message.contains("patrata")) && (message.contains("me") || message.contains("m.e."))) {
            return getMEEligibility(language);
        }
        
        if (message.contains("bsc") && message.contains("mca")) {
            return getBScMCAEligibility(language);
        }
        
        // ==================== FEE FAQS ====================
        if ((message.contains("fee") || message.contains("fees") || message.contains("cost") || message.contains("kharch")) && 
            message.contains("mca")) {
            return getMCAFee(language);
        }
        
        if ((message.contains("fee") || message.contains("fees") || message.contains("cost") || message.contains("kharch")) && 
            (message.contains("me") || message.contains("m.e."))) {
            return getMEFee(language);
        }
        
        if (message.contains("scholarship") || message.contains("scholarship") || message.contains("shishyavrtti")) {
            return getScholarshipInfo(language);
        }
        
        if (message.contains("installment") || message.contains("emi") || message.contains("kist")) {
            return getInstallmentInfo(language);
        }
        
        // ==================== PLACEMENT FAQS ====================
        if ((message.contains("placement") || message.contains("job") || message.contains("nokri") || message.contains("package")) && 
            message.contains("mca")) {
            return getMCAPlacement(language);
        }
        
        if ((message.contains("placement") || message.contains("job") || message.contains("nokri") || message.contains("package")) && 
            (message.contains("me") || message.contains("m.e."))) {
            return getMEPlacement(language);
        }
        
        if (message.contains("highest package") || message.contains("top package") || message.contains("sarvochch package")) {
            return getHighestPackage(language);
        }
        
        if (message.contains("average package") || message.contains("avg package") || message.contains("ausat package")) {
            return getAveragePackage(language);
        }
        
        if (message.contains("companies") || message.contains("recruiters") || message.contains("kampaniya")) {
            return getTopRecruiters(language);
        }
        
        // ==================== BRANCH WISE PLACEMENT ====================
        if (message.contains("mechanical") && message.contains("placement")) {
            return getMechanicalPlacement(language);
        }
        
        if (message.contains("computer") && message.contains("placement")) {
            return getComputerPlacement(language);
        }
        
        if (message.contains("electrical") && message.contains("placement")) {
            return getElectricalPlacement(language);
        }
        
        if (message.contains("civil") && message.contains("placement")) {
            return getCivilPlacement(language);
        }
        
        // ==================== HOSTEL FAQS ====================
        if (message.contains("hostel") || message.contains("chatralay")) {
            return getHostelInfo(language);
        }
        
        // ==================== EXAM FAQS ====================
        if (message.contains("entrance exam") || message.contains("pravesh pariksha")) {
            return getEntranceExamInfo(language);
        }
        
        if (message.contains("exam pattern") || message.contains("pariksha pattern")) {
            return getExamPattern(language);
        }
        
        // ==================== DOCUMENT FAQS ====================
        if (message.contains("document") || message.contains("kagajpatra")) {
            return getDocumentRequired(language);
        }
        
        // ==================== CAREER FAQS ====================
        if (message.contains("career") || message.contains("future") || message.contains("bhavishya")) {
            return getCareerOptions(language);
        }
        
        // ==================== CUT-OFF FAQS ====================
        if (message.contains("cutoff") || message.contains("cut off") || message.contains("kata")) {
            return getCutoffInfo(language);
        }
        
        // ==================== FACILITY FAQS ====================
        if (message.contains("library") || message.contains("pustakalay")) {
            return getLibraryInfo(language);
        }
        
        if (message.contains("lab") || message.contains("prayogshala")) {
            return getLabInfo(language);
        }
        
        if (message.contains("sports") || message.contains("khel")) {
            return getSportsInfo(language);
        }
        
        return null;
    }
    
        private String getMEOverview(String language) {
        if (language.equals("hi")) {
            return """
                M.E. (मास्टर ऑफ इंजीनियरिंग) क्या है?
                
                --------------------------------------------------
                
                M.E. एक 2-वर्षीय स्नातकोत्तर कार्यक्रम है जो इंजीनियरिंग के विभिन्न क्षेत्रों में विशेषज्ञता प्रदान करता है।
                
                मुख्य विशेषताएं:
                • अवधि: 2 वर्ष (4 सेमेस्टर)
                • विशेषज्ञता: कंप्यूटर, मैकेनिकल, इलेक्ट्रिकल, सिविल
                • फीस: ₹95,000 प्रति वर्ष
                • पात्रता: संबंधित शाखा में B.E./B.Tech
                • प्रवेश परीक्षा: GATE / MAH-M.E. CET
                
                करियर विकल्प:
                • डिजाइन इंजीनियर
                • प्रोजेक्ट मैनेजर
                • गुणवत्ता इंजीनियर
                • अनुसंधान एवं विकास
                • उत्पादन इंजीनियर
                
                प्लेसमेंट:
                • औसत पैकेज: 7.1 LPA
                • उच्चतम पैकेज: 15 LPA
                • शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins
                
                अधिक जानकारी के लिए, M.E. प्लेसमेंट या शाखा-विशेष जानकारी के बारे में पूछें।
                """;
        } else if (language.equals("mr")) {
            return """
                M.E. (मास्टर ऑफ इंजिनिअरिंग) म्हणजे काय?
                
                --------------------------------------------------
                
                M.E. हा 2-वर्षीय पदव्युत्तर कार्यक्रम आहे जो अभियांत्रिकीच्या विविध क्षेत्रांमध्ये विशेषज्ञता प्रदान करतो.
                
                मुख्य वैशिष्ट्ये:
                • कालावधी: 2 वर्षे (4 सेमिस्टर)
                • विशेषज्ञता: कॉम्प्युटर, मेकॅनिकल, इलेक्ट्रिकल, सिव्हिल
                • फी: ₹95,000 प्रति वर्ष
                • पात्रता: संबंधित शाखेत B.E./B.Tech
                • प्रवेश परीक्षा: GATE / MAH-M.E. CET
                
                करिअर पर्याय:
                • डिझाइन इंजिनिअर
                • प्रकल्प व्यवस्थापक
                • गुणवत्ता इंजिनिअर
                • संशोधन आणि विकास
                • उत्पादन इंजिनिअर
                
                प्लेसमेंट:
                • सरासरी पॅकेज: 7.1 LPA
                • सर्वोच्च पॅकेज: 15 LPA
                • शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins
                
                अधिक माहितीसाठी, M.E. प्लेसमेंट किंवा शाखा-विशिष्ट माहितीबद्दल विचारा.
                """;
        } else {
            return """
                What is M.E.?
                
                --------------------------------------------------
                
                M.E. (Master of Engineering) is a 2-year postgraduate program specializing in various engineering branches.
                
                Key Features:
                • Duration: 2 Years (4 Semesters)
                • Specializations: Computer, Mechanical, Electrical, Civil
                • Fee: ₹95,000 per year
                • Eligibility: B.E./B.Tech in relevant branch
                • Entrance Exam: GATE / MAH-M.E. CET
                
                Career Options:
                • Design Engineer
                • Project Manager
                • Quality Engineer
                • Research & Development
                • Production Engineer
                
                Placement:
                • Average Package: 7.1 LPA
                • Highest Package: 15 LPA
                • Top Recruiters: L&T, Tata Motors, Mahindra, Bajaj Auto, Cummins
                
                Ask about M.E. placement or branch-specific details.
                """;
        }
    }
    
    private String getMCAMEDifference(String language) {
        if (language.equals("hi")) {
            return """
                MCA और M.E. में क्या अंतर है?
                
                --------------------------------------------------
                
                MCA (मास्टर ऑफ कंप्यूटर एप्लीकेशन्स):
                • फोकस: सॉफ्टवेयर, एप्लीकेशन, आईटी, प्रोग्रामिंग
                • पात्रता: गणित के साथ स्नातक (B.Sc, BCA, B.Com)
                • प्रवेश परीक्षा: MAH-MCA CET
                • करियर: आईटी कंपनियां, सॉफ्टवेयर फर्म
                • औसत पैकेज: 6.5 LPA
                
                M.E. (मास्टर ऑफ इंजीनियरिंग):
                • फोकस: कोर इंजीनियरिंग, डिजाइन, मैन्युफैक्चरिंग
                • पात्रता: B.E./B.Tech (संबंधित शाखा)
                • प्रवेश परीक्षा: GATE / MAH-M.E. CET
                • करियर: कोर इंजीनियरिंग उद्योग, विनिर्माण कंपनियां
                • औसत पैकेज: 7.1 LPA
                
                सारांश:
                • MCA आईटी/सॉफ्टवेयर उद्योग के लिए बेहतर है
                • M.E. कोर इंजीनियरिंग उद्योगों के लिए बेहतर है
                """;
        } else if (language.equals("mr")) {
            return """
                MCA आणि M.E. मध्ये काय फरक आहे?
                
                --------------------------------------------------
                
                MCA (मास्टर ऑफ कॉम्प्युटर ऍप्लिकेशन्स):
                • फोकस: सॉफ्टवेअर, ऍप्लिकेशन्स, आयटी, प्रोग्रामिंग
                • पात्रता: गणितासह पदवीधर (B.Sc, BCA, B.Com)
                • प्रवेश परीक्षा: MAH-MCA CET
                • करिअर: आयटी कंपन्या, सॉफ्टवेअर फर्म
                • सरासरी पॅकेज: 6.5 LPA
                
                M.E. (मास्टर ऑफ इंजिनिअरिंग):
                • फोकस: कोर इंजिनिअरिंग, डिझाइन, उत्पादन
                • पात्रता: B.E./B.Tech (संबंधित शाखा)
                • प्रवेश परीक्षा: GATE / MAH-M.E. CET
                • करिअर: कोर इंजिनिअरिंग उद्योग, उत्पादन कंपन्या
                • सरासरी पॅकेज: 7.1 LPA
                
                सारांश:
                • MCA आयटी/सॉफ्टवेअर उद्योगासाठी चांगले आहे
                • M.E. कोर इंजिनिअरिंग उद्योगांसाठी चांगले आहे
                """;
        } else {
            return """
                Difference between MCA and M.E.?
                
                --------------------------------------------------
                
                MCA (Master of Computer Applications):
                • Focus: Software, Applications, IT, Programming
                • Eligibility: Graduate with Mathematics (B.Sc, BCA, B.Com)
                • Entrance Exam: MAH-MCA CET
                • Career: IT companies, Software firms
                • Average Package: 6.5 LPA
                
                M.E. (Master of Engineering):
                • Focus: Core Engineering, Design, Manufacturing
                • Eligibility: B.E./B.Tech in relevant branch
                • Entrance Exam: GATE / MAH-M.E. CET
                • Career: Core engineering industries, Manufacturing companies
                • Average Package: 7.1 LPA
                
                Summary:
                • MCA is better for IT/Software industry
                • M.E. is better for core engineering industries
                """;
        }
    }
    
    private String getMCAAdmissionProcess(String language) {
        if (language.equals("hi")) {
            return """
                MCA प्रवेश प्रक्रिया:
                
                --------------------------------------------------
                
                1. पात्रता जांचें: स्नातक में न्यूनतम 50% अंक (आरक्षित वर्ग के लिए 45%) और गणित विषय
                
                2. प्रवेश परीक्षा दें: MAH-MCA CET में शामिल हों
                
                3. काउंसलिंग के लिए पंजीकरण करें: CAP काउंसलिंग के लिए ऑनलाइन पंजीकरण
                
                4. दस्तावेज़ सत्यापन: आवश्यक दस्तावेजों के साथ सत्यापन केंद्र पर जाएं
                
                5. सीट आवंटन: मेरिट के आधार पर सीट आवंटन
                
                6. शुल्क जमा करें और प्रवेश की पुष्टि करें
                
                महत्वपूर्ण तिथियां:
                • MAH-MCA CET: अप्रैल 2026
                • आवेदन की अंतिम तिथि: मार्च 2026
                
                अधिक जानकारी के लिए, पात्रता या दस्तावेजों के बारे में पूछें।
                """;
        } else if (language.equals("mr")) {
            return """
                MCA प्रवेश प्रक्रिया:
                
                --------------------------------------------------
                
                1. पात्रता तपासा: पदवीमध्ये किमान 50% गुण (राखीव वर्गासाठी 45%) आणि गणित विषय
                
                2. प्रवेश परीक्षा द्या: MAH-MCA CET मध्ये सहभागी व्हा
                
                3. समुपदेशनासाठी नोंदणी करा: CAP समुपदेशनासाठी ऑनलाइन नोंदणी
                
                4. दस्तऐवज सत्यापन: आवश्यक कागदपत्रांसह सत्यापन केंद्रावर जा
                
                5. जागा वाटप: गुणवत्तेनुसार जागा वाटप
                
                6. शुल्क भरा आणि प्रवेशाची पुष्टी करा
                
                महत्त्वाच्या तारखा:
                • MAH-MCA CET: एप्रिल २०२६
                • अर्जाची अंतिम तारीख: मार्च २०२६
                
                अधिक माहितीसाठी, पात्रता किंवा कागदपत्रांबद्दल विचारा.
                """;
        } else {
            return """
                MCA Admission Process:
                
                --------------------------------------------------
                
                1. Check Eligibility: Minimum 50% in graduation (45% for reserved categories) with Mathematics
                
                2. Appear for Entrance Exam: Take MAH-MCA CET
                
                3. Register for Counseling: Online registration for CAP counseling
                
                4. Document Verification: Visit verification center with required documents
                
                5. Seat Allotment: Seats allotted based on merit
                
                6. Pay Fees and Confirm Admission
                
                Important Dates:
                • MAH-MCA CET: April 2026
                • Application Deadline: March 2026
                
                Ask about eligibility or documents for more details.
                """;
        }
    }
    
    private String getMEAdmissionProcess(String language) {
        if (language.equals("hi")) {
            return """
                M.E. प्रवेश प्रक्रिया:
                
                --------------------------------------------------
                
                1. पात्रता जांचें: संबंधित शाखा में B.E./B.Tech में न्यूनतम 50% अंक
                
                2. प्रवेश परीक्षा दें: GATE या MAH-M.E. CET में शामिल हों
                
                3. काउंसलिंग के लिए पंजीकरण करें
                
                4. दस्तावेज़ सत्यापन
                
                5. सीट आवंटन
                
                6. शुल्क जमा करें
                
                महत्वपूर्ण तिथियां:
                • GATE 2026: फरवरी 2026
                • MAH-M.E. CET: अप्रैल 2026
                """;
        } else if (language.equals("mr")) {
            return """
                M.E. प्रवेश प्रक्रिया:
                
                --------------------------------------------------
                
                1. पात्रता तपासा: संबंधित शाखेत B.E./B.Tech मध्ये किमान 50% गुण
                
                2. प्रवेश परीक्षा द्या: GATE किंवा MAH-M.E. CET मध्ये सहभागी व्हा
                
                3. समुपदेशनासाठी नोंदणी करा
                
                4. दस्तऐवज सत्यापन
                
                5. जागा वाटप
                
                6. शुल्क भरा
                
                महत्त्वाच्या तारखा:
                • GATE २०२६: फेब्रुवारी २०२६
                • MAH-M.E. CET: एप्रिल २०२६
                """;
        } else {
            return """
                M.E. Admission Process:
                
                --------------------------------------------------
                
                1. Check Eligibility: Minimum 50% in B.E./B.Tech in relevant branch
                
                2. Appear for Entrance Exam: Take GATE or MAH-M.E. CET
                
                3. Register for Counseling
                
                4. Document Verification
                
                5. Seat Allotment
                
                6. Pay Fees
                
                Important Dates:
                • GATE 2026: February 2026
                • MAH-M.E. CET: April 2026
                """;
        }
    }
    
    private String getAdmissionDates(String language) {
        if (language.equals("hi")) {
            return """
                प्रवेश की महत्वपूर्ण तिथियां 2026:
                
                --------------------------------------------------
                
                MAH-MCA CET 2026:
                • परीक्षा तिथि: अप्रैल 2026
                • आवेदन की अंतिम तिथि: मार्च 2026
                
                GATE 2026:
                • परीक्षा तिथि: फरवरी 2026
                • आवेदन की अंतिम तिथि: सितंबर 2025
                
                MAH-M.E. CET 2026:
                • परीक्षा तिथि: अप्रैल 2026
                • आवेदन की अंतिम तिथि: मार्च 2026
                
                काउंसलिंग:
                • पहला दौर: मई-जून 2026
                • दूसरा दौर: जुलाई 2026
                
                कक्षाएं शुरू: अगस्त 2026
                """;
        } else if (language.equals("mr")) {
            return """
                प्रवेशाच्या महत्त्वाच्या तारखा २०२६:
                
                --------------------------------------------------
                
                MAH-MCA CET २०२६:
                • परीक्षा तारीख: एप्रिल २०२६
                • अर्जाची अंतिम तारीख: मार्च २०२६
                
                GATE २०२६:
                • परीक्षा तारीख: फेब्रुवारी २०२६
                • अर्जाची अंतिम तारीख: सप्टेंबर २०२५
                
                MAH-M.E. CET २०२६:
                • परीक्षा तारीख: एप्रिल २०२६
                • अर्जाची अंतिम तारीख: मार्च २०२६
                
                समुपदेशन:
                • पहिली फेरी: मे-जून २०२६
                • दुसरी फेरी: जुलै २०२६
                
                वर्ग सुरू: ऑगस्ट २०२६
                """;
        } else {
            return """
                Important Admission Dates 2026:
                
                --------------------------------------------------
                
                MAH-MCA CET 2026:
                • Exam Date: April 2026
                • Application Deadline: March 2026
                
                GATE 2026:
                • Exam Date: February 2026
                • Application Deadline: September 2025
                
                MAH-M.E. CET 2026:
                • Exam Date: April 2026
                • Application Deadline: March 2026
                
                Counseling:
                • Round 1: May-June 2026
                • Round 2: July 2026
                
                Classes Begin: August 2026
                """;
        }
    }
    
    private String getLastDate(String language) {
        if (language.equals("hi")) {
            return """
                आवेदन की अंतिम तिथियां 2026:
                
                --------------------------------------------------
                
                MAH-MCA CET 2026:
                • आवेदन की अंतिम तिथि: 10 मार्च 2026
                
                GATE 2026:
                • आवेदन की अंतिम तिथि: 30 सितंबर 2025
                
                MAH-M.E. CET 2026:
                • आवेदन की अंतिम तिथि: 15 मार्च 2026
                
                कृपया समय पर आवेदन करें। देर से आवेदन स्वीकार नहीं किए जाएंगे।
                """;
        } else if (language.equals("mr")) {
            return """
                अर्जाच्या अंतिम तारखा २०२६:
                
                --------------------------------------------------
                
                MAH-MCA CET २०२६:
                • अर्जाची अंतिम तारीख: १० मार्च २०२६
                
                GATE २०२६:
                • अर्जाची अंतिम तारीख: ३० सप्टेंबर २०२५
                
                MAH-M.E. CET २०२६:
                • अर्जाची अंतिम तारीख: १५ मार्च २०२६
                
                कृपया वेळेत अर्ज करा. उशीरा अर्ज स्वीकारले जाणार नाहीत.
                """;
        } else {
            return """
                Application Deadlines 2026:
                
                --------------------------------------------------
                
                MAH-MCA CET 2026:
                • Last Date: March 10, 2026
                
                GATE 2026:
                • Last Date: September 30, 2025
                
                MAH-M.E. CET 2026:
                • Last Date: March 15, 2026
                
                Please apply on time. Late applications will not be accepted.
                """;
        }
    }
    
    private String getMCAEligibility(String language) {
        if (language.equals("hi")) {
            return """
                MCA पात्रता मानदंड:
                
                --------------------------------------------------
                
                शैक्षणिक योग्यता:
                • किसी मान्यता प्राप्त विश्वविद्यालय से स्नातक की डिग्री
                • न्यूनतम अंक: 50% (सामान्य वर्ग), 45% (आरक्षित वर्ग)
                • 10+2 या स्नातक स्तर पर गणित विषय अनिवार्य
                
                पात्र डिग्रियां:
                • B.Sc (कंप्यूटर साइंस, गणित, सांख्यिकी)
                • BCA (Bachelor of Computer Applications)
                • B.E./B.Tech (कंप्यूटर साइंस, आईटी)
                • B.Com, B.A. (गणित के साथ)
                
                प्रवेश परीक्षा:
                • MAH-MCA CET स्कोर अनिवार्य
                
                कोई आयु सीमा नहीं है।
                """;
        } else if (language.equals("mr")) {
            return """
                MCA पात्रता निकष:
                
                --------------------------------------------------
                
                शैक्षणिक पात्रता:
                • मान्यताप्राप्त विद्यापीठातून पदवी
                • किमान गुण: ५०% (सामान्य वर्ग), ४५% (राखीव वर्ग)
                • १०+२ किंवा पदवी स्तरावर गणित विषय अनिवार्य
                
                पात्र पदव्या:
                • B.Sc (संगणक शास्त्र, गणित, सांख्यिकी)
                • BCA (Bachelor of Computer Applications)
                • B.E./B.Tech (संगणक शास्त्र, आयटी)
                • B.Com, B.A. (गणितासह)
                
                प्रवेश परीक्षा:
                • MAH-MCA CET स्कोर अनिवार्य
                
                वयोमर्यादा नाही.
                """;
        } else {
            return """
                MCA Eligibility Criteria:
                
                --------------------------------------------------
                
                Educational Qualification:
                • Bachelor's degree from recognized university
                • Minimum marks: 50% (General), 45% (Reserved)
                • Mathematics at 10+2 or graduation level mandatory
                
                Eligible Degrees:
                • B.Sc (Computer Science, Mathematics, Statistics)
                • BCA (Bachelor of Computer Applications)
                • B.E./B.Tech (Computer Science, IT)
                • B.Com, B.A. with Mathematics
                
                Entrance Exam:
                • Valid MAH-MCA CET score required
                
                No age limit.
                """;
        }
    }
    
    private String getMEEligibility(String language) {
        if (language.equals("hi")) {
            return """
                M.E. पात्रता मानदंड:
                
                --------------------------------------------------
                
                शैक्षणिक योग्यता:
                • संबंधित शाखा में B.E./B.Tech की डिग्री
                • न्यूनतम अंक: 50% (सामान्य वर्ग), 45% (आरक्षित वर्ग)
                
                पात्र शाखाएं:
                • कंप्यूटर इंजीनियरिंग
                • मैकेनिकल इंजीनियरिंग
                • इलेक्ट्रिकल इंजीनियरिंग
                • सिविल इंजीनियरिंग
                • इलेक्ट्रॉनिक्स इंजीनियरिंग
                • आईटी
                
                प्रवेश परीक्षा:
                • वैध GATE स्कोर या MAH-M.E. CET स्कोर
                
                प्रायोजित उम्मीदवारों के लिए:
                • न्यूनतम 2 वर्ष का कार्य अनुभव
                
                कोई आयु सीमा नहीं है।
                """;
        } else if (language.equals("mr")) {
            return """
                M.E. पात्रता निकष:
                
                --------------------------------------------------
                
                शैक्षणिक पात्रता:
                • संबंधित शाखेत B.E./B.Tech पदवी
                • किमान गुण: ५०% (सामान्य वर्ग), ४५% (राखीव वर्ग)
                
                पात्र शाखा:
                • कॉम्प्युटर इंजिनिअरिंग
                • मेकॅनिकल इंजिनिअरिंग
                • इलेक्ट्रिकल इंजिनिअरिंग
                • सिव्हिल इंजिनिअरिंग
                • इलेक्ट्रॉनिक्स इंजिनिअरिंग
                • आयटी
                
                प्रवेश परीक्षा:
                • वैध GATE स्कोर किंवा MAH-M.E. CET स्कोर
                
                प्रायोजित उमेदवारांसाठी:
                • किमान २ वर्षांचा कामाचा अनुभव
                
                वयोमर्यादा नाही.
                """;
        } else {
            return """
                M.E. Eligibility Criteria:
                
                --------------------------------------------------
                
                Educational Qualification:
                • B.E./B.Tech degree in relevant branch
                • Minimum marks: 50% (General), 45% (Reserved)
                
                Eligible Branches:
                • Computer Engineering
                • Mechanical Engineering
                • Electrical Engineering
                • Civil Engineering
                • Electronics Engineering
                • IT
                
                Entrance Exam:
                • Valid GATE score or MAH-M.E. CET score
                
                For Sponsored Candidates:
                • Minimum 2 years work experience
                
                No age limit.
                """;
        }
    }
    
    private String getBScMCAEligibility(String language) {
        if (language.equals("hi")) {
            return """
                क्या B.Sc छात्र MCA के लिए पात्र हैं?
                
                --------------------------------------------------
                
                हां, B.Sc छात्र MCA के लिए पात्र हैं यदि:
                
                • उन्होंने 10+2 या स्नातक स्तर पर गणित का अध्ययन किया हो
                • न्यूनतम 50% अंक (सामान्य वर्ग) हों
                
                पात्र B.Sc डिग्रियां:
                • B.Sc कंप्यूटर साइंस
                • B.Sc गणित
                • B.Sc सांख्यिकी
                • B.Sc भौतिकी (गणित के साथ)
                • B.Sc आईटी
                
                B.Sc गणित के बिना? फिर आपको स्नातक स्तर पर गणित पूरा करना होगा या पात्र नहीं होंगे।
                """;
        } else if (language.equals("mr")) {
            return """
                B.Sc विद्यार्थी MCA साठी पात्र आहेत का?
                
                --------------------------------------------------
                
                होय, B.Sc विद्यार्थी MCA साठी पात्र आहेत जर:
                
                • त्यांनी १०+२ किंवा पदवी स्तरावर गणिताचा अभ्यास केला असेल
                • किमान ५०% गुण (सामान्य वर्ग) असतील
                
                पात्र B.Sc पदव्या:
                • B.Sc संगणक शास्त्र
                • B.Sc गणित
                • B.Sc सांख्यिकी
                • B.Sc भौतिकशास्त्र (गणितासह)
                • B.Sc आयटी
                
                B.Sc गणिताशिवाय? तर तुम्हाला पदवी स्तरावर गणित पूर्ण करावे लागेल किंवा पात्र राहणार नाही.
                """;
        } else {
            return """
                Can B.Sc students apply for MCA?
                
                --------------------------------------------------
                
                Yes, B.Sc students are eligible for MCA if:
                
                • They have studied Mathematics at 10+2 or graduation level
                • Minimum 50% marks (General category)
                
                Eligible B.Sc Degrees:
                • B.Sc Computer Science
                • B.Sc Mathematics
                • B.Sc Statistics
                • B.Sc Physics (with Mathematics)
                • B.Sc IT
                
                B.Sc without Mathematics? Then you need to complete Mathematics at graduation level or are not eligible.
                """;
        }
    }
    
    private String getMCAFee(String language) {
        if (language.equals("hi")) {
            return """
                MCA फीस संरचना 2026-27:
                
                --------------------------------------------------
                
                ट्यूशन फीस: ₹85,000 प्रति वर्ष
                विकास शुल्क: ₹5,000 प्रति वर्ष
                कुल फीस: ₹90,000 प्रति वर्ष
                
                वैकल्पिक शुल्क:
                • हॉस्टल फीस: ₹40,000 प्रति वर्ष
                • मेस फीस: ₹36,000 प्रति वर्ष
                • परिवहन शुल्क: ₹15,000 प्रति वर्ष
                
                कुल (हॉस्टल सहित): ₹1,30,000 प्रति वर्ष
                
                भुगतान विकल्प:
                • 2 किश्तों में भुगतान किया जा सकता है
                • ऑनलाइन भुगतान की सुविधा उपलब्ध
                
                छात्रवृत्ति उपलब्ध है। "स्कॉलरशिप" टाइप करके जानकारी प्राप्त करें।
                """;
        } else if (language.equals("mr")) {
            return """
                MCA फी रचना २०२६-२७:
                
                --------------------------------------------------
                
                ट्यूशन फी: ₹८५,००० प्रति वर्ष
                विकास शुल्क: ₹५,००० प्रति वर्ष
                एकूण फी: ₹९०,००० प्रति वर्ष
                
                पर्यायी शुल्क:
                • हॉस्टेल फी: ₹४०,००० प्रति वर्ष
                • मेस फी: ₹३६,००० प्रति वर्ष
                • वाहतूक शुल्क: ₹१५,००० प्रति वर्ष
                
                एकूण (हॉस्टेलसह): ₹१,३०,००० प्रति वर्ष
                
                भरणा पर्याय:
                • २ हप्त्यांमध्ये भरणा करता येतो
                • ऑनलाइन भरणा सुविधा उपलब्ध
                
                शिष्यवृत्ती उपलब्ध आहे. "शिष्यवृत्ती" टाइप करून माहिती मिळवा.
                """;
        } else {
            return """
                MCA Fee Structure 2026-27:
                
                --------------------------------------------------
                
                Tuition Fee: ₹85,000 per year
                Development Fee: ₹5,000 per year
                Total Fee: ₹90,000 per year
                
                Optional Fees:
                • Hostel Fee: ₹40,000 per year
                • Mess Fee: ₹36,000 per year
                • Transport Fee: ₹15,000 per year
                
                Total (with Hostel): ₹1,30,000 per year
                
                Payment Options:
                • Can be paid in 2 installments
                • Online payment facility available
                
                Scholarships available. Type "scholarship" for details.
                """;
        }
    }
    
    private String getMEFee(String language) {
        if (language.equals("hi")) {
            return """
                M.E. फीस संरचना 2026-27:
                
                --------------------------------------------------
                
                ट्यूशन फीस: ₹95,000 प्रति वर्ष
                विकास शुल्क: ₹5,000 प्रति वर्ष
                कुल फीस: ₹1,00,000 प्रति वर्ष
                
                वैकल्पिक शुल्क:
                • हॉस्टल फीस: ₹40,000 प्रति वर्ष
                • मेस फीस: ₹36,000 प्रति वर्ष
                
                कुल (हॉस्टल सहित): ₹1,40,000 प्रति वर्ष
                
                भुगतान विकल्प:
                • 2 किश्तों में भुगतान
                • ऑनलाइन भुगतान की सुविधा
                
                छात्रवृत्ति उपलब्ध है। "स्कॉलरशिप" टाइप करें।
                """;
        } else if (language.equals("mr")) {
            return """
                M.E. फी रचना २०२६-२७:
                
                --------------------------------------------------
                
                ट्यूशन फी: ₹९५,००० प्रति वर्ष
                विकास शुल्क: ₹५,००० प्रति वर्ष
                एकूण फी: ₹१,००,००० प्रति वर्ष
                
                पर्यायी शुल्क:
                • हॉस्टेल फी: ₹४०,००० प्रति वर्ष
                • मेस फी: ₹३६,००० प्रति वर्ष
                
                एकूण (हॉस्टेलसह): ₹१,४०,००० प्रति वर्ष
                
                भरणा पर्याय:
                • २ हप्त्यांमध्ये भरणा
                • ऑनलाइन भरणा सुविधा
                
                शिष्यवृत्ती उपलब्ध आहे. "शिष्यवृत्ती" टाइप करा.
                """;
        } else {
            return """
                M.E. Fee Structure 2026-27:
                
                --------------------------------------------------
                
                Tuition Fee: ₹95,000 per year
                Development Fee: ₹5,000 per year
                Total Fee: ₹1,00,000 per year
                
                Optional Fees:
                • Hostel Fee: ₹40,000 per year
                • Mess Fee: ₹36,000 per year
                
                Total (with Hostel): ₹1,40,000 per year
                
                Payment Options:
                • 2 installments
                • Online payment available
                
                Scholarships available. Type "scholarship" for details.
                """;
        }
    }
    
    private String getScholarshipInfo(String language) {
        if (language.equals("hi")) {
            return """
                छात्रवृत्ति योजनाएं 2026-27:
                
                --------------------------------------------------
                
                सरकारी छात्रवृत्ति:
                
                OBC छात्रवृत्ति:
                • पात्रता: OBC वर्ग, पारिवारिक आय < 8 LPA
                • लाभ: 25% फीस में छूट
                
                SC/ST छात्रवृत्ति:
                • पात्रता: SC/ST वर्ग
                • लाभ: 50% फीस में छूट
                
                EBC छात्रवृत्ति:
                • पात्रता: पारिवारिक आय < 1 LPA
                • लाभ: पूर्ण फीस माफी
                
                मेरिट आधारित छात्रवृत्ति:
                • पात्रता: स्नातक में 90%+ अंक
                • राशि: ₹50,000 प्रति वर्ष
                • 10 सीटें उपलब्ध
                
                अल्पसंख्यक छात्रवृत्ति:
                • पात्रता: अल्पसंख्यक समुदाय
                • लाभ: 25% फीस में छूट
                
                आवेदन के लिए प्रवेश कार्यालय से संपर्क करें।
                """;
        } else if (language.equals("mr")) {
            return """
                शिष्यवृत्ती योजना २०२६-२७:
                
                --------------------------------------------------
                
                शासकीय शिष्यवृत्ती:
                
                OBC शिष्यवृत्ती:
                • पात्रता: OBC वर्ग, कौटुंबिक उत्पन्न < ८ LPA
                • लाभ: २५% फी सवलत
                
                SC/ST शिष्यवृत्ती:
                • पात्रता: SC/ST वर्ग
                • लाभ: ५०% फी सवलत
                
                EBC शिष्यवृत्ती:
                • पात्रता: कौटुंबिक उत्पन्न < १ LPA
                • लाभ: संपूर्ण फी माफी
                
                गुणवत्ता आधारित शिष्यवृत्ती:
                • पात्रता: पदवीमध्ये ९०%+ गुण
                • रक्कम: ₹५०,००० प्रति वर्ष
                • १० जागा उपलब्ध
                
                अल्पसंख्याक शिष्यवृत्ती:
                • पात्रता: अल्पसंख्याक समुदाय
                • लाभ: २५% फी सवलत
                
                अर्जासाठी प्रवेश कार्यालयाशी संपर्क साधा.
                """;
        } else {
            return """
                Scholarship Schemes 2026-27:
                
                --------------------------------------------------
                
                Government Scholarships:
                
                OBC Scholarship:
                • Eligibility: OBC, Family income < 8 LPA
                • Benefit: 25% fee concession
                
                SC/ST Scholarship:
                • Eligibility: SC/ST category
                • Benefit: 50% fee concession
                
                EBC Scholarship:
                • Eligibility: Family income < 1 LPA
                • Benefit: Full fee waiver
                
                Merit-based Scholarship:
                • Eligibility: 90%+ in graduation
                • Amount: ₹50,000 per year
                • 10 seats available
                
                Minority Scholarship:
                • Eligibility: Minority community
                • Benefit: 25% fee concession
                
                Contact admission office for application.
                """;
        }
    }
    
    private String getInstallmentInfo(String language) {
        if (language.equals("hi")) {
            return """
                किश्तों में फीस भुगतान:
                
                --------------------------------------------------
                
                हां, आप फीस का भुगतान 2 किश्तों में कर सकते हैं।
                
                MCA:
                • कुल फीस: ₹90,000 प्रति वर्ष
                • पहली किश्त: ₹45,000 (प्रवेश के समय)
                • दूसरी किश्त: ₹45,000 (दिसंबर तक)
                
                M.E.:
                • कुल फीस: ₹1,00,000 प्रति वर्ष
                • पहली किश्त: ₹50,000 (प्रवेश के समय)
                • दूसरी किश्त: ₹50,000 (दिसंबर तक)
                
                हॉस्टल फीस:
                • कुल: ₹40,000 प्रति वर्ष
                • दो किश्तों में भुगतान किया जा सकता है
                
                ऑनलाइन भुगतान की सुविधा उपलब्ध है।
                
                अधिक जानकारी के लिए प्रवेश कार्यालय से संपर्क करें।
                """;
        } else if (language.equals("mr")) {
            return """
                हप्त्यांमध्ये फी भरणा:
                
                --------------------------------------------------
                
                होय, तुम्ही फीचा भरणा २ हप्त्यांमध्ये करू शकता.
                
                MCA:
                • एकूण फी: ₹९०,००० प्रति वर्ष
                • पहिला हप्ता: ₹४५,००० (प्रवेशाच्या वेळी)
                • दुसरा हप्ता: ₹४५,००० (डिसेंबरपर्यंत)
                
                M.E.:
                • एकूण फी: ₹१,००,००० प्रति वर्ष
                • पहिला हप्ता: ₹५०,००० (प्रवेशाच्या वेळी)
                • दुसरा हप्ता: ₹५०,००० (डिसेंबरपर्यंत)
                
                हॉस्टेल फी:
                • एकूण: ₹४०,००० प्रति वर्ष
                • दोन हप्त्यांमध्ये भरणा करता येतो
                
                ऑनलाइन भरणा सुविधा उपलब्ध आहे.
                
                अधिक माहितीसाठी प्रवेश कार्यालयाशी संपर्क साधा.
                """;
        } else {
            return """
                Fee Payment in Installments:
                
                --------------------------------------------------
                
                Yes, you can pay fees in 2 installments.
                
                MCA:
                • Total Fee: ₹90,000 per year
                • 1st Installment: ₹45,000 (at admission)
                • 2nd Installment: ₹45,000 (by December)
                
                M.E.:
                • Total Fee: ₹1,00,000 per year
                • 1st Installment: ₹50,000 (at admission)
                • 2nd Installment: ₹50,000 (by December)
                
                Hostel Fee:
                • Total: ₹40,000 per year
                • Can be paid in 2 installments
                
                Online payment facility available.
                
                Contact admission office for more details.
                """;
        }
    }
    
    private String getMCAPlacement(String language) {
        if (language.equals("hi")) {
            return """
                MCA प्लेसमेंट आंकड़े 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस: 21
                
                शीर्ष भर्तीकर्ता:
                • Infosys (5 students)
                • TCS (4 students)
                • Accenture (3 students)
                • Wipro (3 students)
                • Capgemini (2 students)
                • Cognizant (2 students)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 6.5 LPA
                • उच्चतम पैकेज: 12 LPA
                • पैकेज रेंज: 4.0 - 12.0 LPA
                
                शीर्ष कंपनियां: Infosys, TCS, Accenture, Wipro, Capgemini
                
                वर्षवार आंकड़ों के लिए "MCA प्लेसमेंट 2023" टाइप करें।
                """;
        } else if (language.equals("mr")) {
            return """
                MCA प्लेसमेंट आकडे २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: २१
                
                शीर्ष भर्तीकर्ता:
                • Infosys (५ विद्यार्थी)
                • TCS (४ विद्यार्थी)
                • Accenture (३ विद्यार्थी)
                • Wipro (३ विद्यार्थी)
                • Capgemini (२ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ६.५ LPA
                • सर्वोच्च पॅकेज: १२ LPA
                • पॅकेज रेंज: ४.० - १२.० LPA
                
                शीर्ष कंपन्या: Infosys, TCS, Accenture, Wipro, Capgemini
                
                वर्षनिहाय आकड्यांसाठी "MCA प्लेसमेंट २०२३" टाइप करा.
                """;
        } else {
            return """
                MCA Placement Statistics 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed: 21
                
                Top Recruiters:
                • Infosys (5 students)
                • TCS (4 students)
                • Accenture (3 students)
                • Wipro (3 students)
                • Capgemini (2 students)
                • Cognizant (2 students)
                
                Package Statistics:
                • Average Package: 6.5 LPA
                • Highest Package: 12 LPA
                • Package Range: 4.0 - 12.0 LPA
                
                Top Companies: Infosys, TCS, Accenture, Wipro, Capgemini
                
                Type "MCA placement 2023" for year-wise data.
                """;
        }
    }
    
    private String getMEPlacement(String language) {
        if (language.equals("hi")) {
            return """
                M.E. प्लेसमेंट आंकड़े 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस (सभी शाखाएं): 242
                
                शीर्ष भर्तीकर्ता:
                • L&T (9 students)
                • Tata Motors (7 students)
                • Mahindra (6 students)
                • Bajaj Auto (5 students)
                • Cummins (3 students)
                • Siemens (3 students)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 7.1 LPA
                • उच्चतम पैकेज: 15 LPA
                • पैकेज रेंज: 5.0 - 15.0 LPA
                
                शाखा-वार आंकड़े:
                • मैकेनिकल: 31 छात्र
                • इलेक्ट्रिकल: 125 छात्र
                • कंप्यूटर: 26 छात्र
                • सिविल: 3 छात्र
                
                शाखा-विशेष जानकारी के लिए "मैकेनिकल प्लेसमेंट" टाइप करें।
                """;
        } else if (language.equals("mr")) {
            return """
                M.E. प्लेसमेंट आकडे २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस (सर्व शाखा): २४२
                
                शीर्ष भर्तीकर्ता:
                • L&T (९ विद्यार्थी)
                • Tata Motors (७ विद्यार्थी)
                • Mahindra (६ विद्यार्थी)
                • Bajaj Auto (५ विद्यार्थी)
                • Cummins (३ विद्यार्थी)
                • Siemens (३ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ७.१ LPA
                • सर्वोच्च पॅकेज: १५ LPA
                • पॅकेज रेंज: ५.० - १५.० LPA
                
                शाखा-निहाय आकडे:
                • मेकॅनिकल: ३१ विद्यार्थी
                • इलेक्ट्रिकल: १२५ विद्यार्थी
                • कॉम्प्युटर: २६ विद्यार्थी
                • सिव्हिल: ३ विद्यार्थी
                
                शाखा-विशिष्ट माहितीसाठी "मेकॅनिकल प्लेसमेंट" टाइप करा.
                """;
        } else {
            return """
                M.E. Placement Statistics 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed (All branches): 242
                
                Top Recruiters:
                • L&T (9 students)
                • Tata Motors (7 students)
                • Mahindra (6 students)
                • Bajaj Auto (5 students)
                • Cummins (3 students)
                • Siemens (3 students)
                
                Package Statistics:
                • Average Package: 7.1 LPA
                • Highest Package: 15 LPA
                • Package Range: 5.0 - 15.0 LPA
                
                Branch-wise Data:
                • Mechanical: 31 students
                • Electrical: 125 students
                • Computer: 26 students
                • Civil: 3 students
                
                Type "Mechanical placement" for branch-specific details.
                """;
        }
    }
    
    private String getHighestPackage(String language) {
        if (language.equals("hi")) {
            return """
                उच्चतम प्लेसमेंट पैकेज 2024-25:
                
                --------------------------------------------------
                
                MCA:
                • उच्चतम पैकेज: 12 LPA
                • कंपनी: Infosys, TCS
                
                M.E.:
                • उच्चतम पैकेज: 15 LPA
                • कंपनी: L&T, Tata Motors
                
                शाखा-वार उच्चतम पैकेज:
                • कंप्यूटर इंजीनियरिंग: 14 LPA
                • मैकेनिकल इंजीनियरिंग: 15 LPA
                • इलेक्ट्रिकल इंजीनियरिंग: 13 LPA
                • सिविल इंजीनियरिंग: 8 LPA
                
                कुल मिलाकर उच्चतम पैकेज: 15 LPA (M.E. मैकेनिकल)
                """;
        } else if (language.equals("mr")) {
            return """
                सर्वोच्च प्लेसमेंट पॅकेज २०२४-२५:
                
                --------------------------------------------------
                
                MCA:
                • सर्वोच्च पॅकेज: १२ LPA
                • कंपनी: Infosys, TCS
                
                M.E.:
                • सर्वोच्च पॅकेज: १५ LPA
                • कंपनी: L&T, Tata Motors
                
                शाखा-निहाय सर्वोच्च पॅकेज:
                • कॉम्प्युटर इंजिनिअरिंग: १४ LPA
                • मेकॅनिकल इंजिनिअरिंग: १५ LPA
                • इलेक्ट्रिकल इंजिनिअरिंग: १३ LPA
                • सिव्हिल इंजिनिअरिंग: ८ LPA
                
                एकूण सर्वोच्च पॅकेज: १५ LPA (M.E. मेकॅनिकल)
                """;
        } else {
            return """
                Highest Placement Package 2024-25:
                
                --------------------------------------------------
                
                MCA:
                • Highest Package: 12 LPA
                • Company: Infosys, TCS
                
                M.E.:
                • Highest Package: 15 LPA
                • Company: L&T, Tata Motors
                
                Branch-wise Highest Package:
                • Computer Engineering: 14 LPA
                • Mechanical Engineering: 15 LPA
                • Electrical Engineering: 13 LPA
                • Civil Engineering: 8 LPA
                
                Overall Highest Package: 15 LPA (M.E. Mechanical)
                """;
        }
    }
    
    private String getAveragePackage(String language) {
        if (language.equals("hi")) {
            return """
                औसत प्लेसमेंट पैकेज 2024-25:
                
                --------------------------------------------------
                
                MCA: 6.5 LPA
                M.E.: 7.1 LPA
                
                शाखा-वार औसत पैकेज:
                • कंप्यूटर इंजीनियरिंग: 8.2 LPA
                • मैकेनिकल इंजीनियरिंग: 7.5 LPA
                • इलेक्ट्रिकल इंजीनियरिंग: 7.8 LPA
                • इलेक्ट्रॉनिक्स इंजीनियरिंग: 6.9 LPA
                • सिविल इंजीनियरिंग: 5.5 LPA
                
                कुल मिलाकर औसत पैकेज: 7.1 LPA
                """;
        } else if (language.equals("mr")) {
            return """
                सरासरी प्लेसमेंट पॅकेज २०२४-२५:
                
                --------------------------------------------------
                
                MCA: ६.५ LPA
                M.E.: ७.१ LPA
                
                शाखा-निहाय सरासरी पॅकेज:
                • कॉम्प्युटर इंजिनिअरिंग: ८.२ LPA
                • मेकॅनिकल इंजिनिअरिंग: ७.५ LPA
                • इलेक्ट्रिकल इंजिनिअरिंग: ७.८ LPA
                • इलेक्ट्रॉनिक्स इंजिनिअरिंग: ६.९ LPA
                • सिव्हिल इंजिनिअरिंग: ५.५ LPA
                
                एकूण सरासरी पॅकेज: ७.१ LPA
                """;
        } else {
            return """
                Average Placement Package 2024-25:
                
                --------------------------------------------------
                
                MCA: 6.5 LPA
                M.E.: 7.1 LPA
                
                Branch-wise Average Package:
                • Computer Engineering: 8.2 LPA
                • Mechanical Engineering: 7.5 LPA
                • Electrical Engineering: 7.8 LPA
                • Electronics Engineering: 6.9 LPA
                • Civil Engineering: 5.5 LPA
                
                Overall Average Package: 7.1 LPA
                """;
        }
    }
    
    private String getTopRecruiters(String language) {
        if (language.equals("hi")) {
            return """
                शीर्ष भर्तीकर्ता कंपनियां 2024-25:
                
                --------------------------------------------------
                
                आईटी कंपनियां:
                • Infosys
                • TCS (Tata Consultancy Services)
                • Accenture
                • Wipro
                • Capgemini
                • Cognizant
                • Tech Mahindra
                
                कोर इंजीनियरिंग कंपनियां:
                • L&T (Larsen & Toubro)
                • Tata Motors
                • Mahindra & Mahindra
                • Bajaj Auto
                • Cummins
                • Siemens
                • Bosch
                
                एमएनसी कंपनियां:
                • Microsoft
                • Google (हाल ही में)
                • Amazon
                
                वर्षवार कंपनी डेटा के लिए "MCA प्लेसमेंट 2024" टाइप करें।
                """;
        } else if (language.equals("mr")) {
            return """
                शीर्ष भर्तीकर्ता कंपन्या २०२४-२५:
                
                --------------------------------------------------
                
                आयटी कंपन्या:
                • Infosys
                • TCS (Tata Consultancy Services)
                • Accenture
                • Wipro
                • Capgemini
                • Cognizant
                • Tech Mahindra
                
                कोर इंजिनिअरिंग कंपन्या:
                • L&T (Larsen & Toubro)
                • Tata Motors
                • Mahindra & Mahindra
                • Bajaj Auto
                • Cummins
                • Siemens
                • Bosch
                
                एमएनसी कंपन्या:
                • Microsoft
                • Google (अलीकडील)
                • Amazon
                
                वर्षनिहाय कंपनी डेटासाठी "MCA प्लेसमेंट २०२४" टाइप करा.
                """;
        } else {
            return """
                Top Recruiting Companies 2024-25:
                
                --------------------------------------------------
                
                IT Companies:
                • Infosys
                • TCS (Tata Consultancy Services)
                • Accenture
                • Wipro
                • Capgemini
                • Cognizant
                • Tech Mahindra
                
                Core Engineering Companies:
                • L&T (Larsen & Toubro)
                • Tata Motors
                • Mahindra & Mahindra
                • Bajaj Auto
                • Cummins
                • Siemens
                • Bosch
                
                MNC Companies:
                • Microsoft
                • Google (recent)
                • Amazon
                
                Type "MCA placement 2024" for year-wise company data.
                """;
        }
    }
    
    private String getMechanicalPlacement(String language) {
        if (language.equals("hi")) {
            return """
                मैकेनिकल इंजीनियरिंग प्लेसमेंट 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस: 31
                
                भर्ती करने वाली कंपनियां:
                • L&T (9 students)
                • Tata Motors (7 students)
                • Mahindra (6 students)
                • Bajaj Auto (5 students)
                • Cummins (3 students)
                • Siemens (3 students)
                • Bosch (2 students)
                • Thermax (1 student)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 7.5 LPA
                • उच्चतम पैकेज: 15 LPA
                • पैकेज रेंज: 5.0 - 15.0 LPA
                
                शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra
                """;
        } else if (language.equals("mr")) {
            return """
                मेकॅनिकल इंजिनिअरिंग प्लेसमेंट २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: ३१
                
                भर्ती कंपन्या:
                • L&T (९ विद्यार्थी)
                • Tata Motors (७ विद्यार्थी)
                • Mahindra (६ विद्यार्थी)
                • Bajaj Auto (५ विद्यार्थी)
                • Cummins (३ विद्यार्थी)
                • Siemens (३ विद्यार्थी)
                • Bosch (२ विद्यार्थी)
                • Thermax (१ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ७.५ LPA
                • सर्वोच्च पॅकेज: १५ LPA
                • पॅकेज रेंज: ५.० - १५.० LPA
                
                शीर्ष भर्तीकर्ता: L&T, Tata Motors, Mahindra
                """;
        } else {
            return """
                Mechanical Engineering Placement 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed: 31
                
                Recruiting Companies:
                • L&T (9 students)
                • Tata Motors (7 students)
                • Mahindra (6 students)
                • Bajaj Auto (5 students)
                • Cummins (3 students)
                • Siemens (3 students)
                • Bosch (2 students)
                • Thermax (1 student)
                
                Package Statistics:
                • Average Package: 7.5 LPA
                • Highest Package: 15 LPA
                • Package Range: 5.0 - 15.0 LPA
                
                Top Recruiters: L&T, Tata Motors, Mahindra
                """;
        }
    }
    
    private String getComputerPlacement(String language) {
        if (language.equals("hi")) {
            return """
                कंप्यूटर इंजीनियरिंग प्लेसमेंट 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस: 26
                
                भर्ती करने वाली कंपनियां:
                • Infosys (8 students)
                • TCS (6 students)
                • Microsoft (2 students)
                • Amazon (2 students)
                • Google (1 student)
                • Accenture (3 students)
                • Wipro (2 students)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 8.2 LPA
                • उच्चतम पैकेज: 14 LPA
                • पैकेज रेंज: 5.0 - 14.0 LPA
                
                शीर्ष भर्तीकर्ता: Infosys, TCS, Microsoft
                """;
        } else if (language.equals("mr")) {
            return """
                कॉम्प्युटर इंजिनिअरिंग प्लेसमेंट २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: २६
                
                भर्ती कंपन्या:
                • Infosys (८ विद्यार्थी)
                • TCS (६ विद्यार्थी)
                • Microsoft (२ विद्यार्थी)
                • Amazon (२ विद्यार्थी)
                • Google (१ विद्यार्थी)
                • Accenture (३ विद्यार्थी)
                • Wipro (२ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ८.२ LPA
                • सर्वोच्च पॅकेज: १४ LPA
                • पॅकेज रेंज: ५.० - १४.० LPA
                
                शीर्ष भर्तीकर्ता: Infosys, TCS, Microsoft
                """;
        } else {
            return """
                Computer Engineering Placement 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed: 26
                
                Recruiting Companies:
                • Infosys (8 students)
                • TCS (6 students)
                • Microsoft (2 students)
                • Amazon (2 students)
                • Google (1 student)
                • Accenture (3 students)
                • Wipro (2 students)
                
                Package Statistics:
                • Average Package: 8.2 LPA
                • Highest Package: 14 LPA
                • Package Range: 5.0 - 14.0 LPA
                
                Top Recruiters: Infosys, TCS, Microsoft
                """;
        }
    }
    
    private String getElectricalPlacement(String language) {
        if (language.equals("hi")) {
            return """
                इलेक्ट्रिकल इंजीनियरिंग प्लेसमेंट 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस: 125
                
                भर्ती करने वाली कंपनियां:
                • Siemens (25 students)
                • Cummins (20 students)
                • Bajaj Auto (18 students)
                • L&T (15 students)
                • Tata Motors (12 students)
                • Mahindra (10 students)
                • Bosch (8 students)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 7.8 LPA
                • उच्चतम पैकेज: 13 LPA
                • पैकेज रेंज: 5.0 - 13.0 LPA
                
                शीर्ष भर्तीकर्ता: Siemens, Cummins, Bajaj Auto
                """;
        } else if (language.equals("mr")) {
            return """
                इलेक्ट्रिकल इंजिनिअरिंग प्लेसमेंट २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: १२५
                
                भर्ती कंपन्या:
                • Siemens (२५ विद्यार्थी)
                • Cummins (२० विद्यार्थी)
                • Bajaj Auto (१८ विद्यार्थी)
                • L&T (१५ विद्यार्थी)
                • Tata Motors (१२ विद्यार्थी)
                • Mahindra (१० विद्यार्थी)
                • Bosch (८ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ७.८ LPA
                • सर्वोच्च पॅकेज: १३ LPA
                • पॅकेज रेंज: ५.० - १३.० LPA
                
                शीर्ष भर्तीकर्ता: Siemens, Cummins, Bajaj Auto
                """;
        } else {
            return """
                Electrical Engineering Placement 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed: 125
                
                Recruiting Companies:
                • Siemens (25 students)
                • Cummins (20 students)
                • Bajaj Auto (18 students)
                • L&T (15 students)
                • Tata Motors (12 students)
                • Mahindra (10 students)
                • Bosch (8 students)
                
                Package Statistics:
                • Average Package: 7.8 LPA
                • Highest Package: 13 LPA
                • Package Range: 5.0 - 13.0 LPA
                
                Top Recruiters: Siemens, Cummins, Bajaj Auto
                """;
        }
    }
    
    private String getCivilPlacement(String language) {
        if (language.equals("hi")) {
            return """
                सिविल इंजीनियरिंग प्लेसमेंट 2024-25:
                
                --------------------------------------------------
                
                कुल छात्र प्लेस: 3
                
                भर्ती करने वाली कंपनियां:
                • L&T (2 students)
                • Construction Company (1 student)
                
                पैकेज आंकड़े:
                • औसत पैकेज: 5.5 LPA
                • उच्चतम पैकेज: 8 LPA
                • पैकेज रेंज: 4.0 - 8.0 LPA
                
                शीर्ष भर्तीकर्ता: L&T
                
                नोट: सिविल इंजीनियरिंग के लिए प्लेसमेंट जारी हैं।
                """;
        } else if (language.equals("mr")) {
            return """
                सिव्हिल इंजिनिअरिंग प्लेसमेंट २०२४-२५:
                
                --------------------------------------------------
                
                एकूण विद्यार्थी प्लेस: ३
                
                भर्ती कंपन्या:
                • L&T (२ विद्यार्थी)
                • बांधकाम कंपनी (१ विद्यार्थी)
                
                पॅकेज आकडेवारी:
                • सरासरी पॅकेज: ५.५ LPA
                • सर्वोच्च पॅकेज: ८ LPA
                • पॅकेज रेंज: ४.० - ८.० LPA
                
                शीर्ष भर्तीकर्ता: L&T
                
                नोंद: सिव्हिल इंजिनिअरिंगसाठी प्लेसमेंट सुरू आहेत.
                """;
        } else {
            return """
                Civil Engineering Placement 2024-25:
                
                --------------------------------------------------
                
                Total Students Placed: 3
                
                Recruiting Companies:
                • L&T (2 students)
                • Construction Company (1 student)
                
                Package Statistics:
                • Average Package: 5.5 LPA
                • Highest Package: 8 LPA
                • Package Range: 4.0 - 8.0 LPA
                
                Top Recruiters: L&T
                
                Note: Placements are ongoing for Civil Engineering.
                """;
        }
    }
    
    private String getHostelInfo(String language) {
        if (language.equals("hi")) {
            return """
                हॉस्टल सुविधाएं:
                
                --------------------------------------------------
                
                उपलब्धता:
                • लड़कों और लड़कियों के लिए अलग-अलग हॉस्टल
                • 500+ कमरे उपलब्ध
                
                हॉस्टल फीस:
                • ₹40,000 प्रति वर्ष
                • 2 किश्तों में भुगतान किया जा सकता है
                
                सुविधाएं:
                • 24/7 वाईफाई                • मेस की सुविधा (₹36,000/वर्ष वैकल्पिक)
                • जिम
                • कॉमन रूम
                • इनडोर गेम्स
                • 24/7 सुरक्षा
                
                नियम:
                • हॉस्टल अनिवार्य नहीं है
                • पास के पीजी भी उपलब्ध हैं
                
                आवेदन प्रवेश के समय किया जा सकता है।
                """;
        } else if (language.equals("mr")) {
            return """
                हॉस्टेल सुविधा:
                
                --------------------------------------------------
                
                उपलब्धता:
                • मुले व मुलींसाठी स्वतंत्र हॉस्टेल
                • ५००+ खोल्या उपलब्ध
                
                हॉस्टेल फी:
                • ₹४०,००० प्रति वर्ष
                • २ हप्त्यांमध्ये भरणा करता येतो
                
                सुविधा:
                • २४/७ वायफाय
                • मेस सुविधा (₹३६,०००/वर्ष पर्यायी)
                • जिम
                • कॉमन रूम
                • इनडोर गेम्स
                • २४/७ सुरक्षा
                
                नियम:
                • हॉस्टेल अनिवार्य नाही
                • जवळचे पीजी देखील उपलब्ध आहेत
                
                अर्ज प्रवेशाच्या वेळी करता येतो.
                """;
        } else {
            return """
                Hostel Facilities:
                
                --------------------------------------------------
                
                Availability:
                • Separate hostels for boys and girls
                • 500+ rooms available
                
                Hostel Fee:
                • ₹40,000 per year
                • Can be paid in 2 installments
                
                Facilities:
                • 24/7 WiFi
                • Mess facility (₹36,000/year optional)
                • Gymnasium
                • Common room
                • Indoor games
                • 24/7 security
                
                Rules:
                • Hostel is not compulsory
                • Nearby PGs also available
                
                Application can be done at the time of admission.
                """;
        }
    }
    
    private String getEntranceExamInfo(String language) {
        if (language.equals("hi")) {
            return """
                प्रवेश परीक्षा जानकारी 2026:
                
                --------------------------------------------------
                
                MCA के लिए:
                • परीक्षा: MAH-MCA CET
                • मोड: ऑनलाइन
                • अवधि: 90 मिनट
                • कुल अंक: 100
                • खंड: गणित, तर्कशक्ति, कंप्यूटर अवधारणाएं
                
                M.E. के लिए:
                • परीक्षा: GATE या MAH-M.E. CET
                
                GATE:
                • मोड: ऑनलाइन
                • अवधि: 3 घंटे
                • कुल अंक: 100
                • विषय: संबंधित इंजीनियरिंग शाखा
                
                MAH-M.E. CET:
                • मोड: ऑनलाइन
                • अवधि: 90 मिनट
                • कुल अंक: 100
                
                महत्वपूर्ण तिथियों के लिए "admission dates" टाइप करें।
                """;
        } else if (language.equals("mr")) {
            return """
                प्रवेश परीक्षा माहिती २०२६:
                
                --------------------------------------------------
                
                MCA साठी:
                • परीक्षा: MAH-MCA CET
                • मोड: ऑनलाइन
                • कालावधी: ९० मिनिटे
                • एकूण गुण: १००
                • विभाग: गणित, तर्कशक्ती, संगणक संकल्पना
                
                M.E. साठी:
                • परीक्षा: GATE किंवा MAH-M.E. CET
                
                GATE:
                • मोड: ऑनलाइन
                • कालावधी: ३ तास
                • एकूण गुण: १००
                • विषय: संबंधित इंजिनिअरिंग शाखा
                
                MAH-M.E. CET:
                • मोड: ऑनलाइन
                • कालावधी: ९० मिनिटे
                • एकूण गुण: १००
                
                महत्त्वाच्या तारखांसाठी "प्रवेश तारखा" टाइप करा.
                """;
        } else {
            return """
                Entrance Exam Information 2026:
                
                --------------------------------------------------
                
                For MCA:
                • Exam: MAH-MCA CET
                • Mode: Online
                • Duration: 90 minutes
                • Total Marks: 100
                • Sections: Mathematics, Reasoning, Computer Concepts
                
                For M.E.:
                • Exam: GATE or MAH-M.E. CET
                
                GATE:
                • Mode: Online
                • Duration: 3 hours
                • Total Marks: 100
                • Subject: Relevant engineering branch
                
                MAH-M.E. CET:
                • Mode: Online
                • Duration: 90 minutes
                • Total Marks: 100
                
                Type "admission dates" for important dates.
                """;
        }
    }
    
    private String getExamPattern(String language) {
        if (language.equals("hi")) {
            return """
                MAH-MCA CET परीक्षा पैटर्न:
                
                --------------------------------------------------
                
                कुल प्रश्न: 100
                कुल अंक: 100
                अवधि: 90 मिनट
                प्रश्न प्रकार: बहुविकल्पीय (MCQ)
                
                खंड और अंक वितरण:
                
                1. गणित (गणित और सांख्यिकी): 40 अंक
                   • बीजगणित
                   • ज्यामिति
                   • कलन
                   • सांख्यिकी
                
                2. तर्कशक्ति: 30 अंक
                   • मौखिक तर्क
                   • गैर-मौखिक तर्क
                   • विश्लेषणात्मक तर्क
                
                3. कंप्यूटर अवधारणाएं: 30 अंक
                   • कंप्यूटर मूल बातें
                   • प्रोग्रामिंग अवधारणाएं
                   • डेटाबेस
                   • नेटवर्किंग
                
                नकारात्मक अंकन: नहीं
                """;
        } else if (language.equals("mr")) {
            return """
                MAH-MCA CET परीक्षा पद्धती:
                
                --------------------------------------------------
                
                एकूण प्रश्न: १००
                एकूण गुण: १००
                कालावधी: ९० मिनिटे
                प्रश्न प्रकार: बहुपर्यायी (MCQ)
                
                विभाग आणि गुण वितरण:
                
                १. गणित (गणित आणि सांख्यिकी): ४० गुण
                   • बीजगणित
                   • भूमिती
                   • कलन
                   • सांख्यिकी
                
                २. तर्कशक्ती: ३० गुण
                   • शाब्दिक तर्क
                   • अ-शाब्दिक तर्क
                   • विश्लेषणात्मक तर्क
                
                ३. संगणक संकल्पना: ३० गुण
                   • संगणक मूलभूत
                   • प्रोग्रामिंग संकल्पना
                   • डेटाबेस
                   • नेटवर्किंग
                
                नकारात्मक गुण: नाही
                """;
        } else {
            return """
                MAH-MCA CET Exam Pattern:
                
                --------------------------------------------------
                
                Total Questions: 100
                Total Marks: 100
                Duration: 90 minutes
                Question Type: Multiple Choice (MCQ)
                
                Sections and Marks Distribution:
                
                1. Mathematics (Mathematics & Statistics): 40 Marks
                   • Algebra
                   • Geometry
                   • Calculus
                   • Statistics
                
                2. Reasoning: 30 Marks
                   • Verbal Reasoning
                   • Non-verbal Reasoning
                   • Analytical Reasoning
                
                3. Computer Concepts: 30 Marks
                   • Computer Basics
                   • Programming Concepts
                   • Database
                   • Networking
                
                Negative Marking: No
                """;
        }
    }
    
    private String getDocumentRequired(String language) {
        if (language.equals("hi")) {
            return """
                प्रवेश के लिए आवश्यक दस्तावेज:
                
                --------------------------------------------------
                
                अनिवार्य दस्तावेज:
                • 10वीं मार्कशीट और पासिंग सर्टिफिकेट
                • 12वीं मार्कशीट और पासिंग सर्टिफिकेट
                • स्नातक मार्कशीट (सभी सेमेस्टर)
                • स्नातक डिग्री/प्रोविजनल सर्टिफिकेट
                • स्कूल लीविंग सर्टिफिकेट
                • डोमिसाइल सर्टिफिकेट (निवास प्रमाण पत्र)
                • पासपोर्ट साइज फोटो (6 प्रतियां)
                
                श्रेणी प्रमाण पत्र (यदि लागू हो):
                • जाति प्रमाण पत्र
                • जाति वैधता प्रमाण पत्र
                • गैर-क्रीमी लेयर प्रमाण पत्र
                • ईडब्ल्यूएस प्रमाण पत्र
                
                अन्य दस्तावेज:
                • प्रवेश परीक्षा स्कोर कार्ड
                • CAP राउंड आवंटन पत्र
                • माइग्रेशन सर्टिफिकेट
                • गैप सर्टिफिकेट (यदि लागू हो)
                • आधार कार्ड
                
                सभी दस्तावेजों की मूल और छायाप्रतियां लाएं।
                """;
        } else if (language.equals("mr")) {
            return """
                प्रवेशासाठी आवश्यक कागदपत्रे:
                
                --------------------------------------------------
                
                अनिवार्य कागदपत्रे:
                • १०वी मार्कशीट व पासिंग प्रमाणपत्र
                • १२वी मार्कशीट व पासिंग प्रमाणपत्र
                • पदवी मार्कशीट (सर्व सेमिस्टर)
                • पदवी प्रमाणपत्र / तात्पुरते प्रमाणपत्र
                • शाळा सोडल्याचे प्रमाणपत्र
                • मूळ निवास प्रमाणपत्र
                • पासपोर्ट आकाराचे फोटो (६ प्रती)
                
                श्रेणी प्रमाणपत्रे (लागू असल्यास):
                • जात प्रमाणपत्र
                • जात वैधता प्रमाणपत्र
                • गैर-क्रीमी लेयर प्रमाणपत्र
                • ईडब्ल्यूएस प्रमाणपत्र
                
                इतर कागदपत्रे:
                • प्रवेश परीक्षा स्कोर कार्ड
                • CAP फेरी वाटप पत्र
                • स्थलांतर प्रमाणपत्र
                • गॅप प्रमाणपत्र (लागू असल्यास)
                • आधार कार्ड
                
                सर्व कागदपत्रांच्या मूळ व छायाप्रती आणा.
                """;
        } else {
            return """
                Documents Required for Admission:
                
                --------------------------------------------------
                
                Mandatory Documents:
                • 10th Marksheet & Passing Certificate
                • 12th Marksheet & Passing Certificate
                • Graduation Marksheets (all semesters)
                • Graduation Degree/Provisional Certificate
                • School Leaving Certificate
                • Domicile Certificate
                • Passport size photographs (6 copies)
                
                Category Certificates (if applicable):
                • Caste Certificate
                • Caste Validity Certificate
                • Non-Creamy Layer Certificate
                • EWS Certificate
                
                Other Documents:
                • Entrance Exam Score Card
                • CAP Round Allotment Letter
                • Migration Certificate
                • Gap Certificate (if applicable)
                • Aadhar Card
                
                Bring original and photocopies of all documents.
                """;
        }
    }
    
    private String getCareerOptions(String language) {
        if (language.equals("hi")) {
            return """
                करियर विकल्प:
                
                --------------------------------------------------
                
                MCA के बाद करियर:
                • सॉफ्टवेयर डेवलपर / इंजीनियर
                • वेब डेवलपर
                • डेटा एनालिस्ट / साइंटिस्ट
                • सिस्टम एनालिस्ट
                • डेटाबेस एडमिनिस्ट्रेटर
                • नेटवर्क एडमिनिस्ट्रेटर
                • आईटी कंसल्टेंट
                • मोबाइल ऐप डेवलपर
                • क्लाउड आर्किटेक्ट
                • साइबर सिक्योरिटी एनालिस्ट
                
                M.E. के बाद करियर:
                • डिजाइन इंजीनियर
                • प्रोजेक्ट मैनेजर
                • गुणवत्ता इंजीनियर
                • अनुसंधान एवं विकास इंजीनियर
                • उत्पादन इंजीनियर
                • सलाहकार
                • शिक्षाविद् / प्रोफेसर
                • उद्यमी
                
                उच्च अध्ययन:
                • पीएचडी
                • एमबीए
                """;
        } else if (language.equals("mr")) {
            return """
                करिअर पर्याय:
                
                --------------------------------------------------
                
                MCA नंतर करिअर:
                • सॉफ्टवेअर डेव्हलपर / इंजिनिअर
                • वेब डेव्हलपर
                • डेटा विश्लेषक / शास्त्रज्ञ
                • सिस्टम विश्लेषक
                • डेटाबेस प्रशासक
                • नेटवर्क प्रशासक
                • आयटी सल्लागार
                • मोबाइल ऍप डेव्हलपर
                • क्लाउड आर्किटेक्ट
                • सायबर सिक्युरिटी विश्लेषक
                
                M.E. नंतर करिअर:
                • डिझाइन इंजिनिअर
                • प्रकल्प व्यवस्थापक
                • गुणवत्ता इंजिनिअर
                • संशोधन आणि विकास इंजिनिअर
                • उत्पादन इंजिनिअर
                • सल्लागार
                • शिक्षणतज्ज्ञ / प्राध्यापक
                • उद्योजक
                
                उच्च अभ्यास:
                • पीएचडी
                • एमबीए
                """;
        } else {
            return """
                Career Options:
                
                --------------------------------------------------
                
                After MCA:
                • Software Developer / Engineer
                • Web Developer
                • Data Analyst / Scientist
                • System Analyst
                • Database Administrator
                • Network Administrator
                • IT Consultant
                • Mobile App Developer
                • Cloud Architect
                • Cyber Security Analyst
                
                After M.E.:
                • Design Engineer
                • Project Manager
                • Quality Engineer
                • Research & Development Engineer
                • Production Engineer
                • Consultant
                • Academician / Professor
                • Entrepreneur
                
                Higher Studies:
                • PhD
                • MBA
                """;
        }
    }
    
    private String getCutoffInfo(String language) {
        if (language.equals("hi")) {
            return """
                कट-ऑफ जानकारी 2024:
                
                --------------------------------------------------
                
                MCA कार्यक्रम:
                • सामान्य वर्ग: 85-92 पर्सेंटाइल
                • ओबीसी वर्ग: 75-82 पर्सेंटाइल
                • एससी/एसटी वर्ग: 65-72 पर्सेंटाइल
                
                M.E. कार्यक्रम:
                • सामान्य वर्ग: 80-88 पर्सेंटाइल
                • ओबीसी वर्ग: 70-78 पर्सेंटाइल
                • एससी/एसटी वर्ग: 60-68 पर्सेंटाइल
                
                पिछले वर्ष के रुझान:
                • COEP: 94 पर्सेंटाइल
                • VJTI: 92 पर्सेंटाइल
                • SPIT: 89 पर्सेंटाइल
                • PICT: 87 पर्सेंटाइल
                
                नोट: कट-ऑफ शाखा और कॉलेज के अनुसार बदल सकते हैं।
                """;
        } else if (language.equals("mr")) {
            return """
                कट-ऑफ माहिती २०२४:
                
                --------------------------------------------------
                
                MCA कार्यक्रम:
                • खुली श्रेणी: ८५-९२ पर्सेंटाइल
                • ओबीसी श्रेणी: ७५-८२ पर्सेंटाइल
                • एससी/एसटी श्रेणी: ६५-७२ पर्सेंटाइल
                
                M.E. कार्यक्रम:
                • खुली श्रेणी: ८०-८८ पर्सेंटाइल
                • ओबीसी श्रेणी: ७०-७८ पर्सेंटाइल
                • एससी/एसटी श्रेणी: ६०-६८ पर्सेंटाइल
                
                मागील वर्षांचे ट्रेंड:
                • COEP: ९४ पर्सेंटाइल
                • VJTI: ९२ पर्सेंटाइल
                • SPIT: ८९ पर्सेंटाइल
                • PICT: ८७ पर्सेंटाइल
                
                नोंद: कट-ऑफ शाखा आणि महाविद्यालयानुसार बदलू शकतात.
                """;
        } else {
            return """
                Cut-off Information 2024:
                
                --------------------------------------------------
                
                MCA Program:
                • Open Category: 85-92 percentile
                • OBC Category: 75-82 percentile
                • SC/ST Category: 65-72 percentile
                
                M.E. Program:
                • Open Category: 80-88 percentile
                • OBC Category: 70-78 percentile
                • SC/ST Category: 60-68 percentile
                
                Previous Year Trends:
                • COEP: 94 percentile
                • VJTI: 92 percentile
                • SPIT: 89 percentile
                • PICT: 87 percentile
                
                Note: Cut-offs may vary by branch and college.
                """;
        }
    }
    
    private String getLibraryInfo(String language) {
        if (language.equals("hi")) {
            return """
                पुस्तकालय सुविधाएं:
                
                --------------------------------------------------
                
                केंद्रीय पुस्तकालय:
                • 50,000+ पुस्तकें और जर्नल
                • डिजिटल पुस्तकालय अनुभाग
                • 200 सीटों वाला वाचनालय
                
                संसाधन:
                • ऑनलाइन जर्नल एक्सेस
                • ई-पुस्तकें
                • रिसर्च पेपर
                • प्रश्नोत्तरी पत्र (पिछले वर्षों के)
                
                सुविधाएं:
                • 24/7 वाचनालय (परीक्षा समय में)
                • फोटोकॉपी सुविधा
                • कंप्यूटर अनुभाग
                • वाईफाई
                
                पुस्तकालय समय:
                • सोम-शुक्र: सुबह 8:00 बजे से रात 8:00 बजे तक
                • शनि: सुबह 9:00 बजे से शाम 5:00 बजे तक
                • रवि: बंद
                """;
        } else if (language.equals("mr")) {
            return """
                ग्रंथालय सुविधा:
                
                --------------------------------------------------
                
                केंद्रीय ग्रंथालय:
                • ५०,०००+ पुस्तके आणि जर्नल्स
                • डिजिटल ग्रंथालय विभाग
                • २०० जागांसह वाचनालय
                
                संसाधने:
                • ऑनलाइन जर्नल प्रवेश
                • ई-पुस्तके
                • संशोधन पेपर
                • प्रश्नपत्रिका (मागील वर्षांची)
                
                सुविधा:
                • २४/७ वाचनालय (परीक्षा काळात)
                • छायाप्रत सुविधा
                • संगणक विभाग
                • वायफाय
                
                ग्रंथालय वेळ:
                • सोम-शुक्र: सकाळी ८ ते रात्री ८
                • शनि: सकाळी ९ ते संध्याकाळी ५
                • रवि: बंद
                """;
        } else {
            return """
                Library Facilities:
                
                --------------------------------------------------
                
                Central Library:
                • 50,000+ books and journals
                • Digital library section
                • Reading hall with 200 seats
                
                Resources:
                • Online journal access
                • E-books
                • Research papers
                • Question papers (previous years)
                
                Facilities:
                • 24/7 reading hall (during exams)
                • Photocopy facility
                • Computer section
                • WiFi
                
                Library Hours:
                • Mon-Fri: 8:00 AM to 8:00 PM
                • Sat: 9:00 AM to 5:00 PM
                • Sun: Closed
                """;
        }
    }
    
    private String getLabInfo(String language) {
        if (language.equals("hi")) {
            return """
                प्रयोगशाला सुविधाएं:
                
                --------------------------------------------------
                
                कंप्यूटर प्रयोगशालाएं:
                • 10 प्रयोगशालाएं
                • 300+ आधुनिक कंप्यूटर सिस्टम
                • नवीनतम सॉफ्टवेयर और उपकरण
                • 24/7 इंटरनेट सुविधा
                • हाई-स्पीड वाईफाई
                
                इंजीनियरिंग प्रयोगशालाएं:
                • मैकेनिकल वर्कशॉप
                • इलेक्ट्रिकल लैब
                • इलेक्ट्रॉनिक्स लैब
                • सिविल इंजीनियरिंग लैब
                • अनुसंधान एवं विकास प्रयोगशाला
                
                प्रयोगशाला समय:
                • सोम-शुक्र: सुबह 9:00 बजे से शाम 5:00 बजे तक
                • परियोजना कार्य के लिए विशेष पहुंच
                """;
        } else if (language.equals("mr")) {
            return """
                प्रयोगशाळा सुविधा:
                
                --------------------------------------------------
                
                संगणक प्रयोगशाळा:
                • १० प्रयोगशाळा
                • ३००+ आधुनिक संगणक प्रणाली
                • नवीनतम सॉफ्टवेअर आणि साधने
                • २४/७ इंटरनेट सुविधा
                • हाय-स्पीड वायफाय
                
                अभियांत्रिकी प्रयोगशाळा:
                • मेकॅनिकल वर्कशॉप
                • इलेक्ट्रिकल लॅब
                • इलेक्ट्रॉनिक्स लॅब
                • सिव्हिल इंजिनिअरिंग लॅब
                • संशोधन आणि विकास प्रयोगशाळा
                
                प्रयोगशाळा वेळ:
                • सोम-शुक्र: सकाळी ९ ते संध्याकाळी ५
                • प्रकल्प कार्यासाठी विशेष प्रवेश
                """;
        } else {
            return """
                Laboratory Facilities:
                
                --------------------------------------------------
                
                Computer Laboratories:
                • 10 laboratories
                • 300+ modern computer systems
                • Latest software and tools
                • 24/7 internet facility
                • High-speed WiFi
                
                Engineering Laboratories:
                • Mechanical Workshop
                • Electrical Lab
                • Electronics Lab
                • Civil Engineering Lab
                • Research & Development Lab
                
                Lab Hours:
                • Mon-Fri: 9:00 AM to 5:00 PM
                • Special access for project work
                """;
        }
    }
    
    private String getSportsInfo(String language) {
        if (language.equals("hi")) {
            return """
                खेल सुविधाएं:
                
                --------------------------------------------------
                
                इनडोर खेल:
                • टेबल टेनिस
                • शतरंज
                • कैरम
                • बैडमिंटन
                
                आउटडोर खेल:
                • क्रिकेट
                • फुटबॉल
                • बास्केटबॉल
                • वॉलीबॉल
                • एथलेटिक्स
                
                जिम:
                • आधुनिक जिम उपकरण
                • कार्डियो सेक्शन
                • वेट ट्रेनिंग
                • प्रशिक्षक उपलब्ध
                
                खेल मैदान:
                • बहुउद्देशीय मैदान
                • सिंथेटिक ट्रैक
                • इनडोर हॉल
                
                वार्षिक खेल आयोजन:
                • वार्षिक खेल दिवस
                • अंतर-महाविद्यालय प्रतियोगिताएं
                """;
        } else if (language.equals("mr")) {
            return """
                क्रीडा सुविधा:
                
                --------------------------------------------------
                
                इनडोर खेळ:
                • टेबल टेनिस
                • बुद्धिबळ
                • कॅरम
                • बॅडमिंटन
                
                आउटडोअर खेळ:
                • क्रिकेट
                • फुटबॉल
                • बास्केटबॉल
                • व्हॉलीबॉल
                • ॲथलेटिक्स
                
                जिम:
                • आधुनिक जिम उपकरणे
                • कार्डिओ विभाग
                • वेट ट्रेनिंग
                • प्रशिक्षक उपलब्ध
                
                क्रीडांगण:
                • बहुउद्देशीय मैदान
                • सिंथेटिक ट्रॅक
                • इनडोर हॉल
                
                वार्षिक क्रीडा कार्यक्रम:
                • वार्षिक क्रीडा दिन
                • आंतर-महाविद्यालय स्पर्धा
                """;
        } else {
            return """
                Sports Facilities:
                
                --------------------------------------------------
                
                Indoor Sports:
                • Table Tennis
                • Chess
                • Carrom
                • Badminton
                
                Outdoor Sports:
                • Cricket
                • Football
                • Basketball
                • Volleyball
                • Athletics
                
                Gymnasium:
                • Modern gym equipment
                • Cardio section
                • Weight training
                • Trainer available
                
                Sports Ground:
                • Multipurpose ground
                • Synthetic track
                • Indoor hall
                
                Annual Sports Events:
                • Annual Sports Day
                • Inter-college competitions
                """;
        }
    }
        private String getMCAOverview(String language) {
        if (language.equals("hi")) {
            return "MCA (मास्टर ऑफ कंप्यूटर एप्लीकेशन्स) एक 2-वर्षीय स्नातकोत्तर कार्यक्रम है।";
        } else if (language.equals("mr")) {
            return "MCA (मास्टर ऑफ कॉम्प्युटर ऍप्लिकेशन्स) हा 2-वर्षीय पदव्युत्तर कार्यक्रम आहे.";
        } else {
            return "MCA (Master of Computer Applications) is a 2-year postgraduate program.";
        }

    }
}
