'use strict';

// ── Inline field validation ────────────────────────────────────────────────

const VALIDATORS = {
    login:        { test: v => v.trim().length >= 3,  msg: 'Login must be at least 3 characters' },
    password:     { test: v => v.length >= 6,          msg: 'Password must be at least 6 characters' },
    email:        { test: v => /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v.trim()), msg: 'Enter a valid email address' },
    contactName:  { test: v => v.trim().length >= 1,  msg: 'Name cannot be empty' },
    contactPhone: { test: v => /^[0-9+\-\s()]{10,20}$/.test(v.trim()), msg: 'Enter a valid phone number (10–20 digits)' },
    contactEmail: { test: v => v.trim() === '' || /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v.trim()), msg: 'Enter a valid email address' },
    lastname:     { test: v => v.trim().length >= 1,  msg: 'Last name cannot be empty' },
    file:         { test: v => v.trim().length > 0,   msg: 'Please select a file' },
};

function showError(input, msg) {
    input.classList.add('is-invalid');
    const errorEl = document.getElementById(input.id + 'Error')
                 || input.closest('.form-group')?.querySelector('.field-error');
    if (errorEl) {
        errorEl.textContent = msg;
        errorEl.classList.add('visible');
    }
}

function clearError(input) {
    input.classList.remove('is-invalid');
    const errorEl = document.getElementById(input.id + 'Error')
                 || input.closest('.form-group')?.querySelector('.field-error');
    if (errorEl) errorEl.classList.remove('visible');
}

function validateInput(input) {
    const rule = VALIDATORS[input.name];
    if (!rule) return true;
    if (rule.test(input.value)) {
        clearError(input);
        return true;
    }
    showError(input, rule.msg);
    return false;
}

// ── Attach live validation to all controlled inputs ────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('blur', () => validateInput(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('is-invalid')) validateInput(input);
        });
    });

    // ── Form submit validation ─────────────────────────────────────────────
    document.querySelectorAll('form[novalidate]').forEach(form => {
        form.addEventListener('submit', e => {
            let valid = true;
            form.querySelectorAll('.form-control').forEach(input => {
                if (!validateInput(input)) valid = false;
            });
            if (!valid) e.preventDefault();
        });
    });

    // ── Confirm-dialog for data-confirm links (delete actions) ────────────
    document.querySelectorAll('[data-confirm]').forEach(el => {
        el.addEventListener('click', e => {
            if (!window.confirm(el.dataset.confirm)) e.preventDefault();
        });
    });

    // ── Password strength indicator ────────────────────────────────────────
    const passwordInput = document.getElementById('password');
    if (passwordInput && document.getElementById('bar1')) {
        passwordInput.addEventListener('input', () => {
            updatePasswordStrength(passwordInput.value);
        });
    }

    // ── File upload drag-and-drop ──────────────────────────────────────────
    const uploadArea = document.getElementById('uploadArea');
    if (uploadArea) {
        const fileInput = uploadArea.querySelector('input[type="file"]');
        const label    = document.getElementById('uploadLabel');

        fileInput?.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                label.textContent = fileInput.files[0].name;
                clearError(fileInput);
            }
        });

        uploadArea.addEventListener('dragover', e => {
            e.preventDefault();
            uploadArea.classList.add('drag-over');
        });
        uploadArea.addEventListener('dragleave', () => uploadArea.classList.remove('drag-over'));
        uploadArea.addEventListener('drop', e => {
            e.preventDefault();
            uploadArea.classList.remove('drag-over');
            if (e.dataTransfer.files.length > 0 && fileInput) {
                fileInput.files = e.dataTransfer.files;
                label.textContent = e.dataTransfer.files[0].name;
            }
        });
    }
});

// ── Password strength helper ───────────────────────────────────────────────

function updatePasswordStrength(value) {
    const bars = [
        document.getElementById('bar1'),
        document.getElementById('bar2'),
        document.getElementById('bar3'),
    ];
    if (!bars[0]) return;

    const strength = calcStrength(value);
    const classes  = ['', 'weak', 'medium', 'strong'];

    bars.forEach((bar, i) => {
        bar.className = 'strength-bar';
        if (i < strength) bar.classList.add(classes[strength]);
    });
}

function calcStrength(value) {
    if (value.length < 6) return 0;
    let score = 0;
    if (value.length >= 8) score++;
    if (/[A-Z]/.test(value) && /[a-z]/.test(value)) score++;
    if (/[0-9]/.test(value) && /[^A-Za-z0-9]/.test(value)) score++;
    return Math.max(1, score + 1);
}
