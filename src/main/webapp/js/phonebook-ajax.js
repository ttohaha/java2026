'use strict';

(function () {

    // ── Constants ───────────────────────────────────────────────────────────

    var DEBOUNCE_MS          = 300;
    var MIN_QUERY_LENGTH     = 1;
    var ROW_ANIMATION_MS     = 380;

    // ── DOM references ──────────────────────────────────────────────────────

    var searchInput     = document.getElementById('ajaxSearch');
    var spinner         = document.getElementById('ajaxSearchSpinner');
    var staticBlock     = document.getElementById('staticTable');
    var resultsBlock    = document.getElementById('ajaxResultsBlock');
    var resultsBody     = document.getElementById('ajaxResultsBody');
    var noResults       = document.getElementById('ajaxNoResults');

    if (!searchInput) { return; }

    var searchUrl    = searchInput.getAttribute('data-search-url');
    var debounceTimer = null;

    // ── Live search ─────────────────────────────────────────────────────────

    searchInput.addEventListener('input', function () {
        clearTimeout(debounceTimer);
        var query = searchInput.value.trim();

        if (query.length < MIN_QUERY_LENGTH) {
            showStaticTable();
            return;
        }

        debounceTimer = setTimeout(function () {
            fetchSearchResults(query);
        }, DEBOUNCE_MS);
    });

    searchInput.addEventListener('search', function () {
        if (searchInput.value.trim().length === 0) {
            showStaticTable();
        }
    });

    function fetchSearchResults(query) {
        setSpinner(true);

        var url = searchUrl + '&q=' + encodeURIComponent(query);

        fetch(url, {
            method: 'GET',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Server returned ' + response.status);
            }
            return response.json();
        })
        .then(function (data) {
            renderSearchResults(data.items || []);
        })
        .catch(function (err) {
            console.error('Search request failed:', err);
            showStaticTable();
        })
        .finally(function () {
            setSpinner(false);
        });
    }

    function renderSearchResults(items) {
        resultsBody.innerHTML = '';

        if (items.length === 0) {
            showAjaxResults(true);
            return;
        }

        items.forEach(function (item) {
            resultsBody.appendChild(buildRow(item));
        });

        showAjaxResults(false);
    }

    function buildRow(item) {
        var tr = document.createElement('tr');
        tr.id  = 'entry-row-' + item.id;

        tr.innerHTML =
            '<td><strong>' + escapeHtml(item.name)  + '</strong></td>' +
            '<td>'         + escapeHtml(item.phone) + '</td>' +
            '<td>'         + escapeHtml(item.email) + '</td>' +
            '<td><div class="table-actions">' +
                '<button type="button"' +
                        ' class="btn btn-danger btn-ajax-delete"' +
                        ' data-entry-id="' + item.id + '"' +
                        ' data-confirm="Are you sure you want to delete this contact?"' +
                        ' data-delete-url="' + getDeleteUrl() + '">' +
                    'Delete' +
                '</button>' +
            '</div></td>';

        return tr;
    }

    // ── AJAX delete ─────────────────────────────────────────────────────────

    document.addEventListener('click', function (event) {
        var btn = event.target.closest('.btn-ajax-delete');
        if (!btn) { return; }

        var confirmMsg = btn.getAttribute('data-confirm') || 'Delete this contact?';
        if (!window.confirm(confirmMsg)) { return; }

        var entryId   = btn.getAttribute('data-entry-id');
        var deleteUrl = btn.getAttribute('data-delete-url');

        if (!entryId || !deleteUrl) { return; }

        sendDeleteRequest(entryId, deleteUrl, btn);
    });

    function sendDeleteRequest(entryId, deleteUrl, btn) {
        btn.disabled = true;

        var url = deleteUrl + '&entryId=' + encodeURIComponent(entryId);

        fetch(url, {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Delete failed with status ' + response.status);
            }
            return response.json();
        })
        .then(function (data) {
            if (data.ok) {
                removeRow('entry-row-' + entryId);
            } else {
                btn.disabled = false;
                showInlineError(data.message || 'Failed to delete contact.');
            }
        })
        .catch(function (err) {
            console.error('Delete request failed:', err);
            btn.disabled = false;
            showInlineError('Network error. Please try again.');
        });
    }

    function removeRow(rowId) {
        var row = document.getElementById(rowId);
        if (!row) { return; }

        row.classList.add('row-removing');

        setTimeout(function () {
            if (row.parentNode) {
                row.parentNode.removeChild(row);
            }
            checkEmptyTables();
        }, ROW_ANIMATION_MS);
    }

    function checkEmptyTables() {
        var staticBody  = document.getElementById('entriesTableBody');
        var ajaxVisible = !resultsBlock.hidden;

        if (ajaxVisible && resultsBody.querySelectorAll('tr').length === 0) {
            showAjaxResults(true);
        }

        if (!ajaxVisible && staticBody && staticBody.querySelectorAll('tr').length === 0) {
            staticBlock.innerHTML =
                '<div class="empty-state">' +
                    '<div class="empty-icon">&#128222;</div>' +
                    '<p>Your phone book is empty.</p>' +
                '</div>';
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    function showStaticTable() {
        staticBlock.hidden  = false;
        resultsBlock.hidden = true;
        noResults.hidden    = true;
    }

    function showAjaxResults(empty) {
        staticBlock.hidden  = true;
        resultsBlock.hidden = false;
        noResults.hidden    = !empty;
    }

    function setSpinner(active) {
        if (spinner) { spinner.hidden = !active; }
    }

    function getDeleteUrl() {
        var anyBtn = document.querySelector('.btn-ajax-delete');
        return anyBtn ? anyBtn.getAttribute('data-delete-url') : '';
    }

    function showInlineError(message) {
        var existing = document.getElementById('ajaxDeleteError');
        if (existing) { existing.parentNode.removeChild(existing); }

        var div = document.createElement('div');
        div.id        = 'ajaxDeleteError';
        div.className = 'alert alert-danger';
        div.textContent = message;

        var wrapper = document.querySelector('.table-wrapper');
        if (wrapper) { wrapper.insertAdjacentElement('beforebegin', div); }

        setTimeout(function () {
            if (div.parentNode) { div.parentNode.removeChild(div); }
        }, 4000);
    }

    function escapeHtml(str) {
        if (!str) { return ''; }
        return str
            .replace(/&/g,  '&amp;')
            .replace(/</g,  '&lt;')
            .replace(/>/g,  '&gt;')
            .replace(/"/g,  '&quot;')
            .replace(/'/g,  '&#39;');
    }

}());
