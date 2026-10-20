# PayPulse AI 💳🔄

**AI-Powered Failed Payment Recovery Platform for Indian Merchants**

PayPulse AI helps merchants automatically detect, diagnose, and recover failed payments using machine learning and a rule-based AI agent. Built for the Indian payment ecosystem — supports UPI, Razorpay, net banking, and card payments.

> **Problem:** Indian merchants lose 5–15% of revenue to failed payments (UPI timeouts, gateway errors, insufficient funds, mandate failures). Most go unrecovered.  
> **Solution:** PayPulse predicts which failures are recoverable, explains *why* they failed, recommends optimal recovery strategies, and executes them — all with policy guardrails and a full audit trail.

---

## 🎯 Key Features

| Category | Features |
|----------|----------|
| **ML Prediction Engine** | RandomForest/XGBoost model trained on 8,000 transactions; predicts recovery probability with a custom revenue-recovery scoring metric |
| **AI Diagnosis Agent** | Rule-based agent that analyzes failure reasons, explains root causes, and recommends recovery strategies (retry, payment link, mandate retry, alternative method) |
| **Policy Engine** | Merchant-configurable safety rules — min recovery probability, max retries, amount caps, hard-decline blocking, approval thresholds |
| **Recovery Execution** | Automated recovery with Razorpay payment link creation, UPI payment launching, and batch execution |
| **Borrower Safety Scoring** | 6-factor weighted scoring engine (repayment history, overdue behavior, collateral coverage, etc.) with risk tiers |
| **Loan/Debt Tracker** | Digital loan agreements, collateral management, repayment tracking, and legal notice generation |
| **Smart Alerts** | Calendar-based notifications for overdue loans and upcoming deadlines |
| **Analytics Dashboard** | Revenue at Risk metrics, recovery rate trends, gateway health monitoring, outage radar |
| **Audit Trail** | Full explainability for every AI decision; CSV export for compliance |
| **WhatsApp Simulator** | Customer outreach templates with Hinglish support |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Android App (Kotlin)                        │
│              Jetpack Compose · MVVM · Material 3                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ ┌───────────┐  │
│  │ Overview │ │  Queue   │ │ Insights │ │My Debt │ │ Settings  │  │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └───┬────┘ └─────┬─────┘  │
│       └─────────────┴────────────┴───────────┴────────────┘        │
│                          Retrofit API Client                       │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTP/JSON
┌──────────────────────────────▼──────────────────────────────────────┐
│                     FastAPI Backend (Python)                        │
│                                                                    │
│  ┌──────────────┐    ┌───────────────┐    ┌────────────────────┐   │
│  │ ML Service   │───▶│ AI Diagnosis  │───▶│  Policy Engine     │   │
│  │ (XGBoost /   │    │ Agent         │    │  (Safety Rules)    │   │
│  │ RandomForest)│    │ (Root Cause + │    │                    │   │
│  │              │    │  Strategy)    │    │ approved /         │   │
│  │ recovery_    │    │               │    │ requires_approval /│   │
│  │ probability  │    │ recommendation│    │ blocked            │   │
│  └──────────────┘    └───────────────┘    └────────┬───────────┘   │
│                                                    │               │
│  ┌─────────────────────────────────────────────────▼────────────┐  │
│  │              Recovery Agent (Execute + Audit)                │  │
│  │         Razorpay Payment Links · UPI · Mandate Retry        │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ┌──────────────┐    ┌───────────────┐    ┌────────────────────┐   │
│  │ SQLite DB    │    │ Razorpay API  │    │ Web Dashboard      │   │
│  │ (paypulse.db)│    │ (Webhooks)    │    │ (HTML/JS/Charts)   │   │
│  └──────────────┘    └───────────────┘    └────────────────────┘   │
└────────────────────────────────────────────────────────────────────┘
```

**Pipeline:** `Payment Fails → ML Predicts Recovery Probability → AI Diagnoses & Recommends Strategy → Policy Engine Validates → Recovery Agent Executes → Audit Log Records`

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Python 3** | Backend language |
| **FastAPI** | REST API framework |
| **SQLite** | Database (transactions, policies, audit logs, webhooks) |
| **scikit-learn** | ML pipeline (StandardScaler, OneHotEncoder, ColumnTransformer) |
| **XGBoost** | Gradient boosted model for recovery prediction |
| **pandas / numpy** | Data processing and feature engineering |
| **Pydantic v2** | Request/response validation |
| **Razorpay SDK** | Payment link creation, webhook handling (HMAC SHA256 verification) |
| **Uvicorn** | ASGI server |

### Android
| Technology | Purpose |
|------------|---------|
| **Kotlin** | App language |
| **Jetpack Compose** | Declarative UI (Material 3) |
| **MVVM** | Architecture pattern (ViewModel + StateFlow) |
| **Retrofit 2 + OkHttp3** | HTTP client |
| **Gson** | JSON serialization |
| **SharedPreferences** | Local loan data persistence |

### Web Dashboard
| Technology | Purpose |
|------------|---------|
| **HTML5 + Tailwind CSS** | Dashboard UI |
| **Chart.js** | Data visualizations |
| **Vanilla JavaScript** | API integration and DOM manipulation |

---

## 📊 ML Model Details

- **Training Data:** 8,000 synthetic transactions with realistic Indian payment distributions
- **Features (13):**
  - *Numeric (9):* amount, customer_payment_history, customer_success_rate, retry_count, checkout_abandonment, gateway_health, recent_failure_rate, recent_failure_spike, previous_recovery_history
  - *Categorical (4):* payment_method, gateway, failure_reason, subscription_status
- **Target:** `recovered` (binary classification)
- **Models Compared:** Logistic Regression, RandomForest (220 estimators, max depth 12), XGBoost (260 estimators)
- **Selection Metric:** Custom revenue-recovery score:
  ```
  score = F1×0.40 + ROC-AUC×0.30 + Precision×0.20 + Recall×0.10 − min(FP_cost/50000, 0.25)
  ```
- **Decision Threshold:** 0.55 (tuned above default 0.5 to reduce false positives)
- **Preprocessing:** ColumnTransformer with StandardScaler (numeric) + OneHotEncoder (categorical)

---

## 📂 Project Structure

```
PayPulse/
├── app/                              # Android app module
│   └── src/main/java/com/paypulse/ai/
│       ├── MainActivity.kt           # Compose navigation & entry point
│       ├── data/
│       │   ├── api/                   # Retrofit API service interface
│       │   ├── model/                 # DTOs (Loan, Dashboard, Queue, Insights)
│       │   └── repository/           # Data layer
│       ├── ui/
│       │   ├── components/            # 15+ dialog components
│       │   │   ├── AIDecisionDialog, RecoverySimulatorDialog
│       │   │   ├── RazorpayCheckoutDialog, RazorpayAutoPayDialog
│       │   │   ├── WhatsAppSimulatorDialog, LegalNoticeDialog
│       │   │   └── ...
│       │   ├── screens/               # 7 screens (Overview, Queue, Insights, etc.)
│       │   └── theme/                 # Material 3 theming
│       ├── viewmodel/                 # DashboardViewModel, InsightsViewModel
│       └── utils/                     # Scoring engines, exporters, alerts
│           ├── BorrowerScoringEngine.kt
│           ├── AffordabilityEngine.kt
│           ├── SmartAlertEngine.kt
│           └── ReportExporter.kt
│
├── backend/                           # Python FastAPI backend
│   ├── app/
│   │   ├── main.py                    # API routes (25+ endpoints)
│   │   ├── models.py                  # Pydantic schemas
│   │   ├── database.py                # SQLite operations
│   │   ├── ai_agent.py                # Recovery Diagnosis Agent
│   │   ├── ml_service.py              # ML prediction service
│   │   ├── ml_training.py             # Data generation + model training
│   │   ├── policy_engine.py           # Safety policy evaluation
│   │   ├── recovery_agent.py          # Recovery execution + audit
│   │   ├── razorpay_client.py         # Razorpay API integration
│   │   └── generator.py              # Demo data generators
│   ├── artifacts/                     # Trained model + training data
│   │   ├── recovery_model.joblib
│   │   ├── synthetic_transactions.csv
│   │   └── training_report.json
│   ├── requirements.txt
│   └── .env.example
│
└── frontend/                          # Web dashboard
    ├── index.html
    ├── css/style.css
    └── js/
        ├── app.js                     # Dashboard logic
        └── charts.js                  # Chart.js visualizations
```

---

## 🗄️ Database Schema

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `transactions` | Failed payment records | transaction_id, amount, payment_method, gateway, failure_reason, recovery_probability, status |
| `policies` | Merchant safety rules (single row) | min_recovery_probability, max_retry_count, max_automatic_recovery_amount, block_hard_declines |
| `audit_log` | AI decision audit trail | event_type, transaction_id, status, details (JSON) |
| `webhook_events` | Razorpay webhook storage | event_id, event_name, payload (JSON) |

---

## 🔌 API Endpoints

<details>
<summary><b>Click to expand full API reference (25+ endpoints)</b></summary>

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/overview` | Dashboard overview with all metrics |
| `GET` | `/api/dashboard/summary` | Revenue at risk, recovery rate, counts |
| `GET` | `/api/recovery-queue` | All failed transactions for recovery |
| `GET` | `/api/queue?category=` | Filtered recovery queue |
| `GET` | `/api/insights?time_range=` | Payment & recovery intelligence analytics |
| `GET` | `/api/transactions/{id}` | Single transaction details |
| `GET` | `/api/transactions/{id}/decision` | Full AI decision pipeline |
| `GET` | `/api/transactions/{id}/intelligence` | Transaction intelligence with ML signals |
| `POST` | `/api/predict` | ML recovery probability prediction |
| `POST` | `/api/transactions/{id}/simulate` | Strategy simulation |
| `POST` | `/api/recovery/{id}/execute` | Execute recovery action |
| `POST` | `/api/recovery/{id}/confirm-execute` | Confirm and execute recovery |
| `POST` | `/api/recovery/batch-execute` | Batch recovery execution |
| `GET` | `/api/automation/policies` | Get policy configuration |
| `PATCH` | `/api/automation/policies` | Update policies |
| `POST` | `/api/policies/preview` | Preview policy change impact |
| `GET` | `/api/recovery/workflows` | Recovery workflow details |
| `GET` | `/api/automation/status` | Automation status |
| `GET` | `/api/settings` | Recovery settings |
| `POST` | `/api/settings/policy` | Update merchant policy |
| `GET` | `/api/audit-trail` | Audit trail logs |
| `GET` | `/api/audit-log/export-csv` | Export audit log as CSV |
| `GET` | `/api/telemetry/outage-radar` | Gateway health telemetry |
| `POST` | `/api/demo/trigger-failure` | Simulate a payment failure |
| `POST` | `/api/razorpay/create-link` | Create Razorpay payment link |
| `POST` | `/api/razorpay/webhook` | Razorpay webhook handler |

</details>

---

## 🚀 Getting Started

### Prerequisites
- Python 3.9+
- Android Studio (Hedgehog or later)
- Android SDK 34

### Backend Setup

```bash
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate        # Linux/macOS
# venv\Scripts\activate         # Windows

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env with your Razorpay test keys (optional)

# Train ML model (generates synthetic data + trains model)
python -m app.ml_training

# Start the server
python -m uvicorn app.main:app --host 127.0.0.1 --port 8010 --reload
```

The API will be available at `http://localhost:8010` and the web dashboard at `http://localhost:8010/static/index.html`.

### Android Setup

1. Open the root project in Android Studio
2. Sync Gradle dependencies
3. The app connects to `http://10.0.2.2:8000/api/` (Android emulator → host machine)
4. Run on an emulator with API 24+

> **Note:** If the backend is unreachable, the app falls back to locally generated demo data.

### Web Dashboard

The frontend is served automatically by FastAPI as static files. Access it at `http://localhost:8010/static/index.html` once the backend is running.

---

## 📸 Screens

| Screen | Description |
|--------|-------------|
| **Overview** | Revenue at Risk dashboard, recovery metrics, active recovery counts |
| **Recovery Queue** | Prioritized list of failed payments with AI-recommended actions |
| **Insights** | Payment & Recovery Intelligence — trends, patterns, gateway analysis |
| **My Debt** | Loan tracker with borrower profiles, safety scores, collateral, repayments |
| **Settings** | Policy configuration, automation toggles, workflow management |
| **Calendar** | Smart alerts for upcoming deadlines and overdue payments |

---

## 🔐 Security

- **Razorpay Webhooks:** HMAC SHA256 signature verification
- **Razorpay API:** Basic auth with key ID and secret
- **Environment Variables:** Sensitive keys stored in `.env` (not committed)
- **Policy Guardrails:** Hard-decline blocking, amount caps, approval thresholds prevent unsafe automated recovery

---

## 🗺️ Roadmap

- [ ] Add user authentication (JWT)
- [ ] Dockerize backend deployment
- [ ] Add unit and integration tests
- [ ] Real Razorpay sandbox end-to-end testing
- [ ] Push notifications for recovery status updates
- [ ] Multi-tenant support for SaaS deployment
- [ ] Production-grade ML model with real transaction data

---

## 📄 License

This project is for educational and demonstration purposes.

---

<p align="center">
  Built with ❤️ for the Indian merchant ecosystem
</p>
