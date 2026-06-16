import { get_api, put_api } from "../service/api.js";

// ── Toast ────────────────────────────────────────────────────────────────────

/**
 * Show a temporary notification to the user.
 * @param {string} message  - Text to display.
 * @param {'success'|'error'} type - Visual style.
 */
function showToast(message, type = 'success') {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.className = `toast toast--${type} toast--visible`;
  setTimeout(() => { toast.className = 'toast'; }, 3500);
}

// ── Profile load ─────────────────────────────────────────────────────────────

async function loadProfile() {
  try {
    // Replace 1 with the actual logged-in user id
    const user = await get_api(`/api/users/1`);

    document.getElementById('display-pseudo').textContent  = user.pseudo;
    document.getElementById('display-role').textContent    = user.role;
    document.getElementById('avatar-initials').textContent = user.pseudo.charAt(0).toUpperCase();
    document.getElementById('stat-coins').textContent      = user.coins.toLocaleString();
    document.getElementById('coins').textContent           = user.coins.toLocaleString();

    if (user.createdAt) {
      const date = new Date(user.createdAt);
      document.getElementById('display-joined').textContent =
        'Member since ' + date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
    }

    document.getElementById('pseudo').value        = user.pseudo;
    document.getElementById('email').value         = user.email;
    document.getElementById('theme-toggle').checked = user.theme === 'DARK';
  } catch (e) {
    console.warn('Could not load profile:', e);
  }
}

// ── Edit profile form ────────────────────────────────────────────────────────

document.getElementById('profile-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const body = {
    pseudo: document.getElementById('pseudo').value,
    email:  document.getElementById('email').value,
  };
  try {
    await put_api(`/api/users/1`, body);
    showToast('Profile updated successfully.', 'success');
    loadProfile();
  } catch {
    showToast('Could not update profile. Please try again.', 'error');
  }
});

document.getElementById('cancel-btn').addEventListener('click', () => {
  loadProfile();
});

// ── Theme toggle ─────────────────────────────────────────────────────────────

document.getElementById('theme-toggle').addEventListener('change', async (e) => {
  const theme = e.target.checked ? 'DARK' : 'LIGHT';
  try {
    await put_api(`/api/users/1`, { theme });
  } catch {
    console.warn('Could not update theme.');
  }
});

// ── Password modal ───────────────────────────────────────────────────────────

const modal        = document.getElementById('password-modal');
const passwordForm = document.getElementById('password-form');

function openModal() {
  passwordForm.reset();
  modal.hidden = false;
}

function closeModal() {
  modal.hidden = true;
}

document.getElementById('open-password-modal').addEventListener('click', openModal);
document.getElementById('close-password-modal').addEventListener('click', closeModal);

// Close modal when clicking outside the dialog box
modal.addEventListener('click', (e) => {
  if (e.target === modal) closeModal();
});

// Close modal on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && !modal.hidden) closeModal();
});

passwordForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const pwd     = document.getElementById('password').value;
  const confirm = document.getElementById('password-confirm').value;

  if (!pwd) {
    showToast('Please enter a new password.', 'error');
    return;
  }
  if (pwd !== confirm) {
    showToast('Passwords do not match.', 'error');
    return;
  }

  try {
    await put_api(`/api/users/1`, { password: pwd });
    showToast('Password updated successfully.', 'success');
    closeModal();
  } catch {
    showToast('Could not update password. Please try again.', 'error');
  }
});

// ── Init ─────────────────────────────────────────────────────────────────────

loadProfile();