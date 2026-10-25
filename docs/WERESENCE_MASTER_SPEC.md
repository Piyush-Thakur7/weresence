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
