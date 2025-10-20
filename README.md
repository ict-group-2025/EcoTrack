## 🧱 Xây Dựng Mô Hình File: Ecotrack

```
Ecotrack/
├── 📁 frontend/                 # Giao diện người dùng (app/web)
│   ├── src/
│   ├── assets/
│   ├── package.json
│   └── README.md
│
├── 📁 backend/                  # Server, API, xử lý dữ liệu
│   ├── src/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── models/
│   │   ├── routes/
│   │   └── utils/
│   ├── tests/
│   ├── pom.xml                  # Nếu dùng Java + Maven
│   ├── Dockerfile
│   └── README.md
│
├── 📁 ai_model/                 # Mô hình AI: chatbot + prediction
│   ├── notebooks/
│   │   └── data_training.ipynb
│   ├── model/
│   │   └── prediction_model.pkl
│   ├── src/
│   │   └── chatbot_engine.py
│   └── README.md
│
├── 📁 hardware/                 # Mã và sơ đồ cho thiết bị cảm biến
│   ├── arduino/
│   │   └── sensor_node.ino
│   ├── esp32/
│   │   └── data_sender.ino
│   ├── circuit_diagram.png
│   └── README.md
│
├── 📁 docs/                     # Tài liệu nhóm
│   ├── REQUIREMENTS.md
│   ├── DESIGN_ARCHITECTURE.md
│   ├── API_SPEC.md
│   ├── SCRUM_BOARD.md
│   ├── TEAM_MEMBERS.md
│   └── TEST_PLAN.md
│
├── 📁 scripts/                  # Các script tự động
│   ├── setup.sh
│   ├── deploy.sh
│   └── data_fetch.py
│
├── .gitignore
├── .env.example
├── LICENSE
├── CONTRIBUTING.md              # Hướng dẫn commit, branch, PR
├── README.md                    # Giới thiệu tổng quan
└── project_overview.pdf         # Tổng quan nộp/giới thiệu dự án
```
