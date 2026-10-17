/**
 * Cybersecurity Password Strength Checker
 * Connects frontend UI to pure Java backend analysis engine (/api/check, /api/generate)
 */

document.addEventListener('DOMContentLoaded', () => {
  // DOM Elements - Input Section
  const passwordInput = document.getElementById('passwordInput');
  const togglePasswordBtn = document.getElementById('togglePasswordBtn');
  const eyeIcon = document.getElementById('eyeIcon');
  const eyeOffIcon = document.getElementById('eyeOffIcon');
  const checkBtn = document.getElementById('checkBtn');
  const resetBtn = document.getElementById('resetBtn');
  const charCount = document.getElementById('charCount');
  const liveCheckToggle = document.getElementById('liveCheckToggle');
  const backendStatus = document.getElementById('backendStatus');

  // DOM Elements - Strength Results
  const scoreNum = document.getElementById('scoreNum');
  const scoreVerdict = document.getElementById('scoreVerdict');
  const ratingBadge = document.getElementById('ratingBadge');
  const ratingText = document.getElementById('ratingText');
  const segmentedMeter = document.getElementById('segmentedMeter');
  const feedbackBanner = document.getElementById('feedbackBanner');
  const feedbackText = document.getElementById('feedbackText');

  // DOM Elements - Checklist Rules
  const ruleLength = document.getElementById('ruleLength');
  const ruleUpper = document.getElementById('ruleUpper');
  const ruleLower = document.getElementById('ruleLower');
  const ruleDigit = document.getElementById('ruleDigit');
  const ruleSpecial = document.getElementById('ruleSpecial');

  // DOM Elements - Suggestions
  const suggestionsContent = document.getElementById('suggestionsContent');

  // DOM Elements - Generator
  const lengthSlider = document.getElementById('lengthSlider');
  const lengthValue = document.getElementById('lengthValue');
  const generatedPassword = document.getElementById('generatedPassword');
  const generateBtn = document.getElementById('generateBtn');
  const copyGeneratedBtn = document.getElementById('copyGeneratedBtn');
  const useGeneratedBtn = document.getElementById('useGeneratedBtn');
  const toast = document.getElementById('toast');

  let debounceTimer = null;

  // 1. Initial Health Check with Java Backend
  checkBackendHealth();

  function checkBackendHealth() {
    fetch('/api/health')
      .then(res => res.json())
      .then(data => {
        backendStatus.textContent = 'Java Engine Connected • Port 8080';
        backendStatus.parentElement.style.borderColor = 'rgba(0, 255, 136, 0.4)';
      })
      .catch(() => {
        // Fallback info if running from another port or direct file
        backendStatus.textContent = 'Standby • Connecting to Java...';
      });
  }

  // 2. Show / Hide Password Toggle
  togglePasswordBtn.addEventListener('click', () => {
    const isPassword = passwordInput.getAttribute('type') === 'password';
    passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
    eyeIcon.classList.toggle('hidden', isPassword);
    eyeOffIcon.classList.toggle('hidden', !isPassword);
  });

  // 3. Input Event & Live Sync
  passwordInput.addEventListener('input', () => {
    const len = passwordInput.value.length;
    charCount.textContent = len === 1 ? '1 character' : `${len} characters`;

    if (liveCheckToggle.checked) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        checkPassword(passwordInput.value);
      }, 120); // 120ms debounce for responsive live evaluation
    }
  });

  // 4. "Check Password" Button Click
  checkBtn.addEventListener('click', () => {
    checkPassword(passwordInput.value);
  });

  // 5. "Reset" Button Click
  resetBtn.addEventListener('click', () => {
    resetAll();
  });

  // 6. Password Generator Length Slider
  lengthSlider.addEventListener('input', (e) => {
    lengthValue.textContent = e.target.value;
  });

  // 7. "Generate Secure Password" Button Click
  generateBtn.addEventListener('click', () => {
    const length = lengthSlider.value;
    generatePassword(length);
  });

  // 8. "Copy" Button Click
  copyGeneratedBtn.addEventListener('click', () => {
    const pass = generatedPassword.value;
    if (!pass) return;

    navigator.clipboard.writeText(pass).then(() => {
      showToast('Password copied to clipboard!');
    }).catch(() => {
      // Fallback
      generatedPassword.select();
      document.execCommand('copy');
      showToast('Password copied!');
    });
  });

  // 9. "Test in Checker" Button Click
  useGeneratedBtn.addEventListener('click', () => {
    const pass = generatedPassword.value;
    if (!pass) {
      generatePassword(lengthSlider.value, (pwd) => {
        passwordInput.value = pwd;
        charCount.textContent = `${pwd.length} characters`;
        checkPassword(pwd);
        showToast('Password placed in checker and analyzed!');
      });
      return;
    }

    passwordInput.value = pass;
    charCount.textContent = `${pass.length} characters`;
    checkPassword(pass);
    showToast('Transferred to checker & analyzed!');
  });

  // --------------------------------------------------------------------------
  // Core Communication: Calling Java Backend API
  // --------------------------------------------------------------------------
  async function checkPassword(password) {
    if (!password || password.trim() === '') {
      // Empty input handling
      displayEmptyState();
      return;
    }

    try {
      // Invoke Java backend POST /api/check
      const response = await fetch('/api/check', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ password: password })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      updateUIWithResult(result);

    } catch (err) {
      console.warn('Backend call failed, using client fallback analyzer', err);
      // Client-side fallback analyzer adhering to identical Java logic
      const fallbackResult = clientFallbackAnalyze(password);
      updateUIWithResult(fallbackResult);
    }
  }

  async function generatePassword(length, callback) {
    try {
      const response = await fetch(`/api/generate?length=${length}`);
      if (response.ok) {
        const data = await response.json();
        generatedPassword.value = data.generatedPassword;
        if (callback) callback(data.generatedPassword);
        return;
      }
    } catch (e) {
      console.warn('Using client generator fallback');
    }

    // Fallback generator meeting all 5 requirements
    const gen = clientFallbackGenerate(length);
    generatedPassword.value = gen;
    if (callback) callback(gen);
  }

  // --------------------------------------------------------------------------
  // UI Rendering & State Updates
  // --------------------------------------------------------------------------
  function updateUIWithResult(result) {
    // 1. Update Score & Verdict
    scoreNum.textContent = result.score;
    scoreNum.style.color = getScoreColor(result.score);

    // 2. Rating Badge
    ratingBadge.className = 'rating-badge';
    if (result.rating === 'WEAK') {
      ratingBadge.classList.add('rating-weak');
      ratingText.textContent = 'WEAK';
      scoreVerdict.textContent = 'High vulnerability • Easily compromised';
    } else if (result.rating === 'MEDIUM') {
      ratingBadge.classList.add('rating-medium');
      ratingText.textContent = 'MEDIUM';
      scoreVerdict.textContent = 'Moderate security • Needs improvement';
    } else if (result.rating === 'STRONG') {
      ratingBadge.classList.add('rating-strong');
      ratingText.textContent = 'STRONG';
      scoreVerdict.textContent = 'High security • Cryptographically resilient';
    }

    // 3. Segmented Meter
    segmentedMeter.className = `segmented-meter meter-score-${result.score}`;

    // 4. Update 5-Rule Checklist
    updateChecklistItem(ruleLength, result.hasMinLength);
    updateChecklistItem(ruleUpper, result.hasUppercase);
    updateChecklistItem(ruleLower, result.hasLowercase);
    updateChecklistItem(ruleDigit, result.hasDigit);
    updateChecklistItem(ruleSpecial, result.hasSpecial);

    // 5. Feedback Banner
    feedbackBanner.className = 'feedback-banner';
    if (result.isCommonPassword) {
      feedbackBanner.classList.add('banner-warning');
      feedbackText.textContent = '⚠️ CRITICAL ALERT: Commonly used password detected! Change immediately.';
    } else if (result.score === 5) {
      feedbackBanner.classList.add('banner-success');
      feedbackText.textContent = '🛡️ SHIELD ACTIVATED: Perfect score! Your password satisfies all 5 security rules.';
    } else if (result.score >= 3) {
      feedbackBanner.classList.add('banner-medium');
      feedbackText.textContent = '⚡ Almost there: Address the remaining missing rules below.';
    } else {
      feedbackBanner.classList.add('banner-warning');
      feedbackText.textContent = '⚠️ WEAK PASSWORD: Vulnerable to automated dictionary and brute-force attacks.';
    }

    // 6. Suggestions
    renderSuggestions(result.suggestions, result.score === 5);
  }

  function updateChecklistItem(element, isSatisfied) {
    element.setAttribute('data-satisfied', isSatisfied ? 'true' : 'false');
  }

  function renderSuggestions(suggestions, isPerfectScore) {
    suggestionsContent.innerHTML = '';

    if (isPerfectScore) {
      const congrats = document.createElement('div');
      congrats.className = 'success-congrats';
      congrats.innerHTML = `
        <span class="congrats-icon">🛡️</span>
        <div>
          <strong>Outstanding Defense!</strong>
          <p style="margin-top: 2px; font-size: 0.85rem; color: #a7f3d0;">
            All 5 cybersecurity benchmarks met. Great job practicing secure password hygiene!
          </p>
        </div>
      `;
      suggestionsContent.appendChild(congrats);
      return;
    }

    if (!suggestions || suggestions.length === 0) {
      suggestionsContent.innerHTML = '<p class="empty-suggestions-hint">No suggestions needed.</p>';
      return;
    }

    suggestions.forEach(suggestion => {
      const div = document.createElement('div');
      div.className = 'suggestion-item';
      div.textContent = suggestion;
      suggestionsContent.appendChild(div);
    });
  }

  function displayEmptyState() {
    scoreNum.textContent = '0';
    scoreNum.style.color = '#ffffff';
    ratingBadge.className = 'rating-badge rating-initial';
    ratingText.textContent = 'EMPTY INPUT';
    scoreVerdict.textContent = 'Enter a password to evaluate strength';

    segmentedMeter.className = 'segmented-meter';

    updateChecklistItem(ruleLength, false);
    updateChecklistItem(ruleUpper, false);
    updateChecklistItem(ruleLower, false);
    updateChecklistItem(ruleDigit, false);
    updateChecklistItem(ruleSpecial, false);

    feedbackBanner.className = 'feedback-banner banner-warning';
    feedbackText.textContent = '⚠️ Warning: Password input is empty. Please enter a password.';

    suggestionsContent.innerHTML = `
      <div class="suggestion-item">
        Enter a password containing at least 8 characters, uppercase, lowercase, numbers, and symbols.
      </div>
    `;
  }

  function resetAll() {
    passwordInput.value = '';
    charCount.textContent = '0 characters';
    scoreNum.textContent = '0';
    scoreNum.style.color = '#ffffff';
    ratingBadge.className = 'rating-badge rating-initial';
    ratingText.textContent = 'AWAITING INPUT';
    scoreVerdict.textContent = 'Enter a password to evaluate strength';

    segmentedMeter.className = 'segmented-meter';

    updateChecklistItem(ruleLength, false);
    updateChecklistItem(ruleUpper, false);
    updateChecklistItem(ruleLower, false);
    updateChecklistItem(ruleDigit, false);
    updateChecklistItem(ruleSpecial, false);

    feedbackBanner.className = 'feedback-banner banner-neutral';
    feedbackText.textContent = 'Please type a password to begin Java security analysis.';

    suggestionsContent.innerHTML = `
      <p class="empty-suggestions-hint">
        Missing rules will appear here as actionable tips to strengthen your password.
      </p>
    `;

    passwordInput.focus();
  }

  function getScoreColor(score) {
    if (score <= 2) return 'var(--red-neon)';
    if (score <= 4) return 'var(--amber-neon)';
    return 'var(--green-neon)';
  }

  function showToast(message) {
    toast.textContent = message;
    toast.classList.remove('hidden');
    setTimeout(() => {
      toast.classList.add('hidden');
    }, 2500);
  }

  // --------------------------------------------------------------------------
  // Fallback Analyzer (Mirrors Java PasswordAnalyzer logic exactly)
  // --------------------------------------------------------------------------
  function clientFallbackAnalyze(password) {
    const specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`";
    let hasMinLength = password.length >= 8;
    let hasUpper = false;
    let hasLower = false;
    let hasDigit = false;
    let hasSpecial = false;

    for (let i = 0; i < password.length; i++) {
      const c = password.charAt(i);
      if (c >= 'A' && c <= 'Z') hasUpper = true;
      else if (c >= 'a' && c <= 'z') hasLower = true;
      else if (c >= '0' && c <= '9') hasDigit = true;
      else if (specialChars.indexOf(c) !== -1) hasSpecial = true;
    }

    let score = 0;
    if (hasMinLength) score++;
    if (hasUpper) score++;
    if (hasLower) score++;
    if (hasDigit) score++;
    if (hasSpecial) score++;

    let rating = score <= 2 ? 'WEAK' : score <= 4 ? 'MEDIUM' : 'STRONG';
    const suggestions = [];
    if (!hasMinLength) suggestions.push(`Add at least 8 characters (currently ${password.length}).`);
    if (!hasUpper) suggestions.push('Include at least one uppercase letter (A–Z).');
    if (!hasLower) suggestions.push('Include at least one lowercase letter (a–z).');
    if (!hasDigit) suggestions.push('Add at least one number (0–9).');
    if (!hasSpecial) suggestions.push('Include at least one special character (e.g. @, #, $, !, %).');

    const common = ["password", "123456", "12345678", "qwerty", "admin", "password123"];
    const isCommon = common.includes(password.toLowerCase().trim());

    return {
      score,
      rating,
      hasMinLength,
      hasUppercase: hasUpper,
      hasLowercase: hasLower,
      hasDigit,
      hasSpecial,
      isCommonPassword: isCommon,
      suggestions
    };
  }

  function clientFallbackGenerate(length = 16) {
    const u = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const l = "abcdefghijklmnopqrstuvwxyz";
    const d = "0123456789";
    const s = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    const all = u + l + d + s;

    let pwd = [
      u.charAt(Math.floor(Math.random() * u.length)),
      l.charAt(Math.floor(Math.random() * l.length)),
      d.charAt(Math.floor(Math.random() * d.length)),
      s.charAt(Math.floor(Math.random() * s.length))
    ];

    for (let i = 4; i < length; i++) {
      pwd.push(all.charAt(Math.floor(Math.random() * all.length)));
    }

    // Shuffle
    for (let i = pwd.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [pwd[i], pwd[j]] = [pwd[j], pwd[i]];
    }

    return pwd.join('');
  }
});
