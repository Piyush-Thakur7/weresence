# 🌟 WeResence: The Sovereign Hybrid Privacy Messenger + AI Assistant
## Master System Specification & Architecture

### 👑 Brand Identity
* **Application Name:** **WeResence**
* **Brand:** Resence Ecosystem (`resence.in`)
* **Founder & Visionary:** Piyush Singh (Piyush Thakur)

---

### 🧠 1. Resence AI Built-In Engine (Meta AI Style)
Directly integrated from Piyush's `aiassistant` stack:
* **Dedicated Contact:** `@ResenceAI` (Always pinned with a dynamic glowing gradient ring).
* **Inline Tagging:** In any direct chat or group, type `@ai explain this` or `@ai summarize` to invoke Resence AI.
* **Multimodal Vision:** Drop textbook diagrams, notes, code snippets, or images -> Processed by **Gemini 2.5 Flash Vision**.
* **Real-time Voice Mode:** Interactive Siri-style audio brainstormer powered by **Groq Llama 3.3 70B** (500 tokens/sec speed).
* **Offline AI Behavior (Strict Grey Mode):** 
  * Internet band hote hi AI ka glowing avatar **100% GREY (Disabled)** ho jayega.
  * AI chat input box freeze/lock ho jayega.
  * **ZERO QUEUEING:** Koi message queue me nahi jayega. User ko clear dikhega ki AI offline hai aur internet aane par hi activate hoga.

---

### 📦 2. APK Size & Target Budget (45MB – 60MB Sweet Spot)
* **Target Size:** **~48 MB – 55 MB**
* **Why this is the perfect size:**
  * Allows bundling pre-compiled GPU shaders for **butter-smooth 120 FPS animations** on Android 14, 15, and 16.
  * Allows embedding clean vector Lottie animations (the glowing Resence AI orb).
  * Bundles SQLCipher native cryptographic libraries (`.so`) for ARM64 and ARMv7 architectures.

---

### 🏎️ 3. Universal Budget Optimization (3GB / 4GB RAM Phones)
* **RAM Ceiling:** Idle < 35MB | Active Chat < 55MB.
* **Storage Footprint:** Private Sandboxed Vault (`/data/user/0/com.resence.weresence/vault/`) with `.nomedia` and `.enc` extension.
* **Battery Drain:** Passive port listening (0.0% battery consumption when phone is asleep).
* **Display Engine:** Hardware-accelerated Skia/Impeller renderer, zero jank on 60Hz and 120Hz displays.

---

### 🔒 4. Core Security & Stealth Features
* **Dual Transport:** WebSockets (Online) 🔄 Port 8901 Data SMS (Offline, 0% leak to Google Messages).
* **Media Policy:** Online = Photos/Videos/Audio/Text; Offline = Dynamic Lock 🔒 on media, Text SMS active.
* **Delete for Everyone:** Works on BOTH Internet and Offline SMS via 20-byte Revoke Packet.
* **Anti-Screenshot & Recording:** Android `FLAG_SECURE` enforced across all screens.
* **Ghost Mode:** Dual-PIN authentication:
  * Master PIN (`1234`): Unlocks real encrypted chats.
  * Decoy PIN (`0000`): Unlocks fake harmless screen with dummy conversations.

---

### 👥 5. Groups & Communities Architecture (100% Online Rule)
* **Strict Online Isolation:**
  * **Groups and Communities run 100% ONLINE ONLY (Wi-Fi / Mobile Data).**
  * **Offline stealth SMS logic is strictly isolated to 1-on-1 direct conversations.**
  * *Reason:* Sending a group message offline to 50 members would require 50 individual carrier SMS messages, burning the user's 100 daily SMS limit in 2 sends. By isolating SMS to 1-on-1 chats, the user's daily carrier quota is 100% protected.
* **Community Hierarchy (WhatsApp Reverse-Engineered Model):**
  * **Parent Community:** e.g. "GLBIM Greater Noida" (`is_parent: true`)
  * **Announcement Channel:** Only admins can broadcast college-wide notices (`announcement_only: true`).
  * **Sub-Groups:** Topic-based class groups (e.g. "BCA AI/ML Sem 3", "Hackathon Lab").
* **Offline Behavior in Groups:**
  * Shows a subtle WhatsApp inline badge: `[ 📡 Groups paused while offline • 1-on-1 DMs active ]`.
  * All incoming group chats sync silently the second internet reconnects.

---

### 👤 6. Identity, Profile & Settings Architecture
* **Primary Key = Phone Number:**
  * Exactly ONE unique WeResence profile per phone number.
  * Contact matching works automatically via the user's phonebook (WhatsApp model).
  * If a contact has a phone number, you can chat with them immediately.
* **`ResenceID` (@handle):**
  * Unique public alphanumeric username (e.g. `@piyush`) used for mentions, community roles, and AI tagging.
* **Profile Attributes:**
  * Profile Picture (Avatar)
  * Display Name
  * Verified Phone Number (Permanent Account Anchor)
  * `ResenceID` (@handle)
  * **User Bio / Status** (Custom text, e.g. "Available on WeResence")
* **Linked Google Account:**
  * Optional link for fast 1-tap re-login and encrypted Google Drive chat backups.
* **Master Settings Screen Breakdown:**
  1. **My Profile**: Edit photo, display name, and bio. View verified phone number and `ResenceID`.
  2. **Linked Google Account**: Connect or disconnect Google account for cloud backup.
  3. **Privacy & Vault**:
     * Anti-Screenshot & Screen Recording (`FLAG_SECURE` - Always On)
     * Vault PIN Configuration (Master PIN `1234` / Decoy Ghost PIN `0000`)
     * Blocked Contacts
  4. **Offline SIM & Telephony**:
     * Dual-SIM selector (Choose SIM 1 or SIM 2 for offline SMS)
     * Daily Offline SMS Meter (e.g. `84 / 100 used today`, auto-resets at midnight)
  5. **Appearance**:
     * Light Mode / Minimalist Dark Mode / OLED Pitch Black
  6. **Storage & Data**:
     * Manage local encrypted media cache (`.nomedia`)
  7. **Log Out**:
     * Unlocks/locks device session, securely signs out, requiring phone verification to re-enter.
