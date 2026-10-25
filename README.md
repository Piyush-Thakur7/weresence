# WeResence

> Simple, Sovereign Messaging & AI Assistant. Works online and offline.

---

## Overview

**WeResence** is a lightweight, privacy-focused messenger built to keep people connected anytime, anywhere — even when internet coverage completely drops.

- **Dual-Engine Delivery**: Messages automatically switch to silent, direct offline delivery when data turns off, and switch back to high-speed multimedia when online.
- **Resence AI**: Your personal built-in smart assistant right inside your chat list. Answers questions, writes summaries, and helps you think. Automatically rests when offline to conserve battery and data.
- **True Privacy**: Anti-screenshot and anti-screen recording built into every screen. Messages can be deleted for everyone at any time.
- **Secret Vault**: An invisible secondary PIN to hide sensitive chats whenever you need.

---

## Architecture & Core Modules

- `/core/telephony`: Direct offline SMS message sender and receiver (silent background transport).
- `/core/network`: Real-time network monitor for seamless automatic switching.
- `/core/crypto`: End-to-end device encryption envelopes.
- `/core/dispatcher`: Message router directing traffic between internet and offline channels.
- `/core/ai`: Resence AI companion engine with strict offline pause.
- `/schema`: Local encrypted database schema.

---

## Distribution

- Official Website: [weresence.resence.in](https://weresence.resence.in)
- Web Repository: [Piyush-Thakur7/weresenceweb](https://github.com/Piyush-Thakur7/weresenceweb)
- Architected & Created by: **Piyush Singh** ([resence.in](https://resence.in))
