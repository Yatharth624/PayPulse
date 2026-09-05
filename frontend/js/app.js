const API_BASE = "";
let selectedTransactionId = null;

function formatPercent(value) {
  return `${Math.round(value * 100)}%`;
}

function recommendationLabel(value) {
  return String(value || "")
    .replaceAll("_", " ")
    .replace(/\b\w/g, (char) => char.toUpperCase());
}

function statusMeta(transaction) {
  if (transaction.recovery_probability < 0.45) {
    return { label: "Escalated", color: "red", action: "View" };
  }
  if (transaction.amount >= 25000) {
    return { label: "Awaiting approval", color: "amber", action: "Review" };
  }
  return { label: "Ready", color: "emerald", action: "Review" };
}

function queueItem(transaction) {
  const meta = statusMeta(transaction);
  const priority =
    transaction.recovery_probability >= 0.7
      ? '<span class="bg-amber-100 text-amber-800 text-[9px] font-extrabold px-2 py-0.5 rounded-md">HIGH PRIORITY</span>'
      : "";

  return `
    <div class="bg-white rounded-2xl p-3.5 border border-slate-100 shadow-sm relative overflow-hidden flex flex-col space-y-2">
      <div class="absolute left-0 top-0 bottom-0 w-1 bg-${meta.color === "red" ? "red-500" : meta.color === "amber" ? "amber-500" : "sky-600"}"></div>
      <div class="flex items-center justify-between ml-1">
        <div class="flex items-center space-x-2">
          <span class="font-extrabold text-slate-900 text-xs">${transaction.transaction_id}</span>
          ${priority}
        </div>
        <span class="bg-${meta.color}-50 text-${meta.color}-700 text-[10px] font-bold px-2 py-0.5 rounded-full flex items-center gap-1 border border-${meta.color}-100">
          <span class="w-1.5 h-1.5 rounded-full bg-${meta.color}-500"></span> ${meta.label}
        </span>
      </div>
      <div class="ml-1 text-xs space-y-0.5">
        <p class="font-bold text-slate-900">${transaction.amount.toLocaleString("en-IN", {
          style: "currency",
          currency: "INR",
          maximumFractionDigits: 0,
        })} · ${recommendationLabel(transaction.failure_reason)} · Prob: ${formatPercent(transaction.recovery_probability)}</p>
        <p class="text-sky-600 font-extrabold">Potential Recovery: ${transaction.potential_recovery_formatted}</p>
        <p class="text-slate-600">Rec: ${recommendationLabel(transaction.recommended_strategy)}</p>
      </div>
      <div class="flex justify-end ml-1">
        <button onclick="openIncidentModal('${transaction.transaction_id}')" class="${meta.color === "red" ? "bg-transparent border border-slate-300 text-slate-700 hover:bg-slate-50" : "bg-sky-600 text-white"} font-bold text-xs px-3.5 py-1.5 rounded-xl flex items-center gap-1 shadow">
          <span>${meta.action}</span>
          <i data-lucide="arrow-right" class="w-3.5 h-3.5"></i>
        </button>
      </div>
    </div>
  `;
}

async function loadDashboard() {
  const summary = await fetch(`${API_BASE}/api/dashboard/summary`).then((res) => res.json());
  document.getElementById("val-at-risk").textContent = summary.revenue_at_risk_formatted;
  document.getElementById("val-recoverable").textContent = summary.expected_recovery_formatted;
  document.getElementById("val-rate").textContent = formatPercent(summary.recovery_rate_estimate);
}

async function loadQueue() {
  const data = await fetch(`${API_BASE}/api/recovery-queue?limit=10`).then((res) => res.json());
  const container = document.getElementById("queue-list-container");
  if (!container) return;
  container.innerHTML = data.transactions.map(queueItem).join("");
  if (window.lucide) lucide.createIcons();
}

async function openIncidentModal(transactionId) {
  selectedTransactionId = transactionId || selectedTransactionId;
  const modal = document.getElementById("incident-modal");
  modal.classList.remove("hidden");

  if (!selectedTransactionId) return;
  const decision = await fetch(`${API_BASE}/api/transactions/${selectedTransactionId}/decision`).then((res) => res.json());
  document.getElementById("modal-title").textContent = `${decision.transaction.transaction_id} Recovery Decision`;
  document.getElementById("modal-root-cause").textContent =
    `${decision.ai_diagnosis.diagnosis_title} ${decision.ai_diagnosis.explanation} Policy result: ${recommendationLabel(decision.policy_evaluation.decision)}.`;
}

function closeIncidentModal() {
  document.getElementById("incident-modal").classList.add("hidden");
}

async function triggerAIRecovery() {
  if (!selectedTransactionId) return;
  const button = document.getElementById("btn-trigger-action");
  button.disabled = true;
  button.querySelector("span").textContent = "Executing through policy engine...";
  try {
    await fetch(`${API_BASE}/api/recovery/${selectedTransactionId}/execute`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ approved_by: "frontend-policy-flow" }),
    });
    button.querySelector("span").textContent = "Recovery action recorded";
    await loadQueue();
  } catch {
    button.querySelector("span").textContent = "Policy blocked automatic execution";
  } finally {
    setTimeout(() => {
      button.disabled = false;
      button.querySelector("span").textContent = "Execute Bounded AI Recovery Sequence";
      closeIncidentModal();
    }, 1200);
  }
}

function switchTab(tabName) {
  ["overview", "queue", "insights", "settings"].forEach((name) => {
    document.getElementById(`tab-${name}`)?.classList.toggle("hidden", name !== tabName);
    document.getElementById(`nav-${name}`)?.classList.toggle("active", name === tabName);
  });
  if (tabName === "queue") loadQueue();
}

window.openIncidentModal = openIncidentModal;
window.closeIncidentModal = closeIncidentModal;
window.triggerAIRecovery = triggerAIRecovery;
window.switchTab = switchTab;

document.addEventListener("DOMContentLoaded", async () => {
  if (window.lucide) lucide.createIcons();
  await Promise.all([loadDashboard(), loadQueue()]);
});
