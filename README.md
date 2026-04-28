# FlowLedger 📱💸

FlowLedger is a modern, premium, secure, and privacy-first personal finance tracker built natively for Android. It is designed to help you quickly log expenses, track your budget limits, and visualize your spending seamlessly.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Room Database](https://img.shields.io/badge/Room-SQLite-blue?style=for-the-badge)

## 🚀 Download & Install

You can install the latest version of the app directly from this repository:

📥 **[Download FlowLedger.apk](https://github.com/githubmofo/FlowLedger/raw/main/FlowLedger.apk)**

**Installation Steps:**
1. Download the `FlowLedger.apk` file to your Android device.
2. Open the downloaded file.
3. If prompted, allow your file manager to **"Install unknown apps"**.
4. Click **Install** and enjoy!

---

## ✨ Features

- **Blazing Fast Logging**: Add expenses and incomes in seconds.
- **Budget Limits**: Set custom Daily, Weekly, and Monthly spending limits. The app dynamically alerts you when you are nearing your limits.
- **Advanced Insights**: Interactive Pie Charts and category-by-category allocation breakdowns to understand where your money goes.
- **Smart UI Navigation**: Featuring a liquid morph navigation bar, glassmorphism design, and 60fps animations.
- **Privacy First (Local-Only)**: We believe financial data belongs to you. FlowLedger has `allowBackup` disabled to prevent unauthorized cloud syncing. All data is securely stored locally using a parameterized Room database.
- **Dark Mode Support**: Multiple visual themes tailored for battery saving and eye comfort.

---

## 🛠️ Requirements to Build from Source

If you wish to download the source code and build it yourself, ensure you meet the following requirements:

- **Android Studio** (Koala or later recommended)
- **Java Development Kit (JDK) 17** (Automatically provisioned by the Gradle Toolchain)
- **Android SDK Platform API 34**
- **Gradle 8.7+** (Wrapper included in the repository)

---

## 💻 How to Build & Run

Everything you need to compile the app is included in the project.

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/FlowLedger.git
cd FlowLedger
```

### 2. Open in Android Studio
- Open Android Studio and select **Open**.
- Navigate to the cloned `FlowLedger` directory and select it.
- Allow Android Studio to sync the Gradle files.

### 3. Build & Run
- Connect your Android device via USB or start an Emulator.
- Click the **Run 'app'** button (Play icon) in the top toolbar.

Alternatively, you can build an APK from the terminal:
```bash
# On Windows
gradlew assembleDebug

# On macOS/Linux
./gradlew assembleDebug
```
The compiled APK will be available in `/app/build/outputs/apk/debug/`.

---

## 🛡️ Security Audit & Architecture
FlowLedger employs rigorous security and architectural standards:
- **No SQLite Injection:** `Room` DAOs handle all SQL parameters using safe binding.
- **UI Thread Safety:** All heavy calculations (aggregation, queries, chart updates) are executed via asynchronous thread pools to prevent frame drops.
- **Anti-Extraction:** `android:allowBackup="false"` is strictly enforced to prevent external ADB backup extraction.

---

## 📄 License
This project is open-source. Feel free to use and modify it for your personal use.
