# PayPulse AI Backend

FastAPI backend for PayPulse AI revenue recovery.

## Run

```powershell
cd D:\PayPulse\backend
python -m pip install -r requirements.txt
python -m app.ml_training
python -m uvicorn app.main:app --host 127.0.0.1 --port 8010
```

Open the existing frontend through the same FastAPI server:

```text
http://127.0.0.1:8010/
```

## Razorpay Test Mode

Set these environment variables on the server only:

```text
RAZORPAY_KEY_ID
RAZORPAY_KEY_SECRET
RAZORPAY_WEBHOOK_SECRET
```

The frontend never receives Razorpay secrets. Webhooks are accepted at:

```text
POST /api/razorpay/webhook
```

The webhook handler validates the raw request body with `X-Razorpay-Signature` before processing.

## Architecture

```text
ML predicts -> AI explains/recommends -> Policy Engine validates -> Recovery Engine executes -> Audit Log records
```

The ML model returns recovery probability and potential recovery only. It never executes a financial action.
