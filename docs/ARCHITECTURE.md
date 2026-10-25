# 🛡️ StealthChat: Hybrid Internet + Invisible Data-SMS Messenger
## Architecture & Blueprint Specification

### 1. The Core Innovation
StealthChat operates on a **Dual-Transport Hybrid Architecture**:
1. **Primary Transport (Online):** High-speed WebSockets / HTTPS Cloud Gateway (Zero carrier cost, real-time).
2. **Fallback Transport (Offline):** **Port-Directed Data SMS (`SmsManager.sendDataMessage`)**.

---

### 2. Why Data SMS Does NOT Leak into Default Messaging Apps
```
                      [ Outgoing Message: "Hey Piyush" ]
                                      │
                         [ AES-256-GCM Encrypted ]
                                      │
                                [ Network Check ]
                               /                 \
                     [ Online? ]                 [ Offline? ]
                         │                            │
             (HTTPS / WebSocket API)      (Android SmsManager.sendDataMessage)
                         │                            │
                  Cloud Server               Target Port: 8901 (Binary Bytes)
                         │                            │
                         │                   Cellular GSM Control Channel
                         │                            │
                         │                    Receiver Phone
                         │                   /              \
                         │        [ Normal SMS Inbox ]   [ StealthChat App ]
                         │          (Port 0 listener)     (Port 8901 listener)
                         │                  │                     │
                         │            ❌ IGNORED!          ✅ INTERCEPTED!
                         │            (0% Leak)                   │
                         │                                 [ AES-256 Decrypt ]
                         │                                        │
                         └────────────────────────────────────────┴──► [ Chat Screen ]
```

* Standard SMS (Port 0) goes to Google Messages / Samsung Messages.
* **Data SMS (Port 8901)** is invisible to standard SMS apps because they only listen on standard text ports. Only StealthChat's registered `DATA_SMS_RECEIVED` intent filter captures the binary payload.

---

### 3. Key Components Extracted:
* `core/telephony/DataSmsSender.kt`: Dispatches 133-byte binary segments directly to the receiver's phone number on secret port 8901.
* `core/telephony/DataSmsReceiver.kt`: Intercepts port-directed incoming bytes silently in the background.
* `core/network/ConnectionManager.kt`: Real-time network watcher with instant fallback triggers.
* `core/crypto/AesCryptoEnvelope.kt`: AES-256-GCM authenticated encryption.
* `core/dispatcher/HybridMessageDispatcher.kt`: The router deciding transport based on link telemetry.
* `schema/chat_database.sql`: SQLite schema tracking messages, delivery status, and transport medium (`INTERNET` vs `DATA_SMS`).
