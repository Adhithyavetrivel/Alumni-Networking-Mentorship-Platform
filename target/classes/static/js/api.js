/**
 * Alumni Networking Platform - Core API & UI Helper Library
 */
const API = {
  baseUrl: '',

  getCurrentUser() {
    try {
      const userStr = localStorage.getItem('alumni_user');
      return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
      return null;
    }
  },

  setCurrentUser(user) {
    if (user) {
      localStorage.setItem('alumni_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('alumni_user');
    }
  },

  async request(endpoint, options = {}) {
    const config = {
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(this.baseUrl + endpoint, config);
      const isJson = response.headers.get('content-type')?.includes('application/json');
      const data = isJson ? await response.json() : await response.text();

      if (!response.ok) {
        let errorMsg = 'Request failed';
        if (data && typeof data === 'object') {
          errorMsg = data.message || data.error || (data.details ? data.details.join(', ') : 'Request failed');
        } else if (typeof data === 'string' && data.length < 150) {
          errorMsg = data;
        }

        if (response.status === 401) {
          this.setCurrentUser(null);
          // If on a protected page, redirect to login
          if (!window.location.pathname.endsWith('login.html') && 
              !window.location.pathname.endsWith('register.html') && 
              !window.location.pathname.endsWith('index.html') &&
              window.location.pathname !== '/') {
            window.location.href = '/login.html?expired=true';
          }
        }
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error('API Error:', err);
      throw err;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  },

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  },

  put(endpoint, body = {}) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  },

  async logout() {
    try {
      await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    } catch (e) {
      console.warn('Logout request failed', e);
    }
    this.setCurrentUser(null);
    window.location.href = '/login.html';
  },

  requireAuth(allowedRoles = []) {
    const user = this.getCurrentUser();
    if (!user) {
      window.location.href = '/login.html';
      return null;
    }
    if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
      alert('Access Denied: You do not have permission to view this page.');
      if (user.role === 'STUDENT') window.location.href = '/student-dashboard.html';
      else if (user.role === 'ALUMNI') window.location.href = '/alumni-dashboard.html';
      else if (user.role === 'ADMIN') window.location.href = '/admin-dashboard.html';
      else window.location.href = '/login.html';
      return null;
    }
    return user;
  },

  showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
      container.style.zIndex = '9999';
      document.body.appendChild(container);
    }

    const toastId = 'toast_' + Date.now();
    const bgClass = type === 'success' ? 'bg-success text-white' : (type === 'error' || type === 'danger' ? 'bg-danger text-white' : 'bg-primary text-white');
    const icon = type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill';

    const toastHtml = `
      <div id="${toastId}" class="toast align-items-center ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
          <div class="toast-body d-flex align-items-center gap-2">
            <i class="bi ${icon}"></i>
            <span>${message}</span>
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    `;

    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastEl = document.getElementById(toastId);
    if (window.bootstrap) {
      const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
      toast.show();
      toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
    } else {
      setTimeout(() => toastEl.remove(), 4000);
    }
  },

  formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
      const d = new Date(dateString);
      return d.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (e) {
      return dateString;
    }
  },

  renderNavbar(activePage = '') {
    const navPlaceholder = document.getElementById('navbar-placeholder');
    if (!navPlaceholder) return;

    const user = this.getCurrentUser();
    let authNavHtml = '';

    if (user) {
      let dashboardLink = '/student-dashboard.html';
      if (user.role === 'ALUMNI') dashboardLink = '/alumni-dashboard.html';
      else if (user.role === 'ADMIN') dashboardLink = '/admin-dashboard.html';

      authNavHtml = `
        <div class="d-flex align-items-center gap-3">
          <a href="${dashboardLink}" class="btn btn-outline-primary btn-sm">
            <i class="bi bi-speedometer2 me-1"></i> Dashboard
          </a>
          <div class="dropdown">
            <button class="btn btn-light dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3" type="button" data-bs-toggle="dropdown">
              <span class="badge bg-primary rounded-circle p-1"><i class="bi bi-person-fill text-white"></i></span>
              <span class="fw-semibold small">${user.name}</span>
              <span class="badge bg-secondary small">${user.role}</span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm">
              <li><h6 class="dropdown-header">${user.email}</h6></li>
              ${user.role === 'STUDENT' ? '<li><a class="dropdown-item" href="/student-profile.html"><i class="bi bi-person me-2"></i>My Profile</a></li>' : ''}
              ${user.role === 'ALUMNI' ? '<li><a class="dropdown-item" href="/alumni-profile.html"><i class="bi bi-person me-2"></i>My Profile</a></li>' : ''}
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="API.logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
            </ul>
          </div>
        </div>
      `;
    } else {
      authNavHtml = `
        <div class="d-flex align-items-center gap-2">
          <a href="/login.html" class="btn btn-outline-primary btn-sm px-3">Login</a>
          <a href="/register.html" class="btn btn-primary btn-sm px-3">Register</a>
        </div>
      `;
    }

    navPlaceholder.innerHTML = `
      <nav class="navbar navbar-expand-lg navbar-custom sticky-top">
        <div class="container-fluid px-4">
          <a class="navbar-brand" href="/index.html">
            <i class="bi bi-mortarboard-fill"></i>
            <span>AlumniNet</span>
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
              <li class="nav-item">
                <a class="nav-link ${activePage === 'home' ? 'active fw-bold text-primary' : ''}" href="/index.html">Home</a>
              </li>
              ${user && user.role === 'STUDENT' ? `
                <li class="nav-item"><a class="nav-link ${activePage === 'find-mentors' ? 'active fw-bold text-primary' : ''}" href="/find-mentors.html">Find Mentors</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'requests' ? 'active fw-bold text-primary' : ''}" href="/mentorship-requests.html">My Requests</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'sessions' ? 'active fw-bold text-primary' : ''}" href="/sessions.html">Sessions</a></li>
              ` : ''}
              ${user && user.role === 'ALUMNI' ? `
                <li class="nav-item"><a class="nav-link ${activePage === 'incoming-requests' ? 'active fw-bold text-primary' : ''}" href="/incoming-requests.html">Incoming Requests</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'mentees' ? 'active fw-bold text-primary' : ''}" href="/mentees.html">Mentees</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'sessions' ? 'active fw-bold text-primary' : ''}" href="/alumni-sessions.html">Sessions</a></li>
              ` : ''}
              ${user && user.role === 'ADMIN' ? `
                <li class="nav-item"><a class="nav-link ${activePage === 'students' ? 'active fw-bold text-primary' : ''}" href="/manage-students.html">Manage Students</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'alumni' ? 'active fw-bold text-primary' : ''}" href="/manage-alumni.html">Manage Alumni</a></li>
                <li class="nav-item"><a class="nav-link ${activePage === 'reports' ? 'active fw-bold text-primary' : ''}" href="/manage-reports.html">Reports</a></li>
              ` : ''}
            </ul>
            ${authNavHtml}
          </div>
        </div>
      </nav>
    `;
  },

  renderSidebar(role, activePage = '') {
    const sidebarPlaceholder = document.getElementById('sidebar-placeholder');
    if (!sidebarPlaceholder) return;

    let navItems = [];
    if (role === 'STUDENT') {
      navItems = [
        { label: 'Dashboard', icon: 'bi-grid-1x2', href: '/student-dashboard.html', id: 'dashboard' },
        { label: 'My Profile', icon: 'bi-person', href: '/student-profile.html', id: 'profile' },
        { label: 'Find Mentors', icon: 'bi-search', href: '/find-mentors.html', id: 'find-mentors' },
        { label: 'Mentorship Requests', icon: 'bi-send', href: '/mentorship-requests.html', id: 'requests' },
        { label: 'Sessions & Feedback', icon: 'bi-calendar-check', href: '/sessions.html', id: 'sessions' }
      ];
    } else if (role === 'ALUMNI') {
      navItems = [
        { label: 'Dashboard', icon: 'bi-grid-1x2', href: '/alumni-dashboard.html', id: 'dashboard' },
        { label: 'Professional Profile', icon: 'bi-briefcase', href: '/alumni-profile.html', id: 'profile' },
        { label: 'Incoming Requests', icon: 'bi-inbox', href: '/incoming-requests.html', id: 'requests' },
        { label: 'Active Mentees', icon: 'bi-people', href: '/mentees.html', id: 'mentees' },
        { label: 'Mentoring Sessions', icon: 'bi-calendar-event', href: '/alumni-sessions.html', id: 'sessions' }
      ];
    } else if (role === 'ADMIN') {
      navItems = [
        { label: 'Overview Dashboard', icon: 'bi-speedometer2', href: '/admin-dashboard.html', id: 'dashboard' },
        { label: 'Manage Students', icon: 'bi-mortarboard', href: '/manage-students.html', id: 'students' },
        { label: 'Manage Alumni', icon: 'bi-person-check', href: '/manage-alumni.html', id: 'alumni' },
        { label: 'Reported Profiles', icon: 'bi-shield-exclamation', href: '/manage-reports.html', id: 'reports' }
      ];
    }

    const navListHtml = navItems.map(item => `
      <li>
        <a href="${item.href}" class="nav-link ${activePage === item.id ? 'active' : ''}">
          <i class="bi ${item.icon}"></i>
          <span>${item.label}</span>
        </a>
      </li>
    `).join('');

    sidebarPlaceholder.innerHTML = `
      <div class="sidebar">
        <div class="sidebar-heading">${role} PORTAL</div>
        <ul class="sidebar-nav">
          ${navListHtml}
        </ul>
        <div class="mt-auto pt-3 border-top">
          <a href="javascript:void(0)" onclick="API.logout()" class="nav-link text-danger">
            <i class="bi bi-box-arrow-left"></i>
            <span>Log Out</span>
          </a>
        </div>
      </div>
    `;
  }
};
