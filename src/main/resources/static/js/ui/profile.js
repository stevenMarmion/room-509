import { get_api, put_api } from "../service/api.js";

async function loadProfile() {
  try {
    // Replace 1 with the actual logged-in user id
    const user = await get_api(`/api/users/1`).then(r => r.json()); // to change when auth is implemented

    document.getElementById('display-pseudo').textContent   = user.pseudo;
    document.getElementById('display-role').textContent     = user.role;
    document.getElementById('avatar-initials').textContent  = user.pseudo.charAt(0).toUpperCase();
    document.getElementById('stat-coins').textContent       = user.coins.toLocaleString();
    document.getElementById('coins').textContent            = user.coins.toLocaleString();

    if (user.createdAt) {
      const date = new Date(user.createdAt);
      document.getElementById('display-joined').textContent =
        'Member since ' + date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
    }

    document.getElementById('pseudo').value = user.pseudo;
    document.getElementById('email').value  = user.email;
    document.getElementById('theme-toggle').checked = user.theme === 'DARK';
  } catch (e) {
    console.warn('Could not load profile:', e);
  }
}

document.getElementById('profile-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const pwd = document.getElementById('password').value;
  const confirm = document.getElementById('password-confirm').value;
  if (pwd && pwd !== confirm) {
    alert('Passwords do not match.');
    return;
  }
  const body = {
    pseudo: document.getElementById('pseudo').value,
    email:  document.getElementById('email').value,
  };
  if (pwd) body.password = pwd;

  try {
    await put_api(`/api/users/1`, body);
    alert('Profile updated!');
    loadProfile();
  } catch (e) {
    alert('Error updating profile.');
  }
});

document.getElementById('theme-toggle').addEventListener('change', async (e) => {
  const theme = e.target.checked ? 'DARK' : 'LIGHT';
  try {
    await put_api(`/api/users/1`, theme);
  } catch (err) {
    console.warn('Could not update theme:', err);
  }
});

loadProfile();