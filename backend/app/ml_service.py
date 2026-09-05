from __future__ import annotations

from typing import Any, Dict

import joblib
import pandas as pd

from .ml_training import FEATURE_COLUMNS, MODEL_PATH, ensure_model_artifacts


class RecoveryModelService:
    def __init__(self) -> None:
        ensure_model_artifacts()
        self.pipeline = joblib.load(MODEL_PATH)

    def predict(self, features: Dict[str, Any]) -> Dict[str, float]:
        row = {column: features[column] for column in FEATURE_COLUMNS}
        frame = pd.DataFrame([row])
        probability = float(self.pipeline.predict_proba(frame)[0][1])
        amount = float(features["amount"])
        return {
            "recovery_probability": round(probability, 4),
            "potential_recovery": round(amount * probability, 2),
        }
