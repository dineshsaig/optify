/* Optify — main.js */

function copyToClipboard(text) {
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text).then(() => markCopied()).catch(() => fallbackCopy(text));
  } else {
    fallbackCopy(text);
  }
}

function fallbackCopy(text) {
  const el = document.createElement('textarea');
  el.value = text;
  el.style.position = 'fixed';
  el.style.opacity = '0';
  document.body.appendChild(el);
  el.select();
  document.execCommand('copy');
  document.body.removeChild(el);
  markCopied();
}

function markCopied() {
  showToast('✓ Link copied to clipboard!');
  const btn = document.querySelector('.share-btn-copy');
  if (!btn) return;
  const orig = btn.innerHTML;
  btn.classList.add('copied');
  btn.innerHTML = '✓ Copied!';
  setTimeout(() => {
    btn.classList.remove('copied');
    btn.innerHTML = orig;
  }, 2200);
}

function showToast(msg) {
  let toast = document.getElementById('global-toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'global-toast';
    toast.className = 'toast';
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.classList.add('show');
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('show'), 2800);
}
