const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});

function activate(checkboxElem, id) {
    var isEnabled = checkboxElem.checked;
    var row = $(checkboxElem).closest('tr');
    $.ajax({
        url: ctx.ajaxUrl + id + '/enable',
        type: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({enabled: isEnabled}),
        success: function () {
            if (isEnabled) {
                row.removeClass('inactive-user');
            } else {
                row.addClass('inactive-user');
            }
        },
        error: function () {
            checkboxElem.checked = !isEnabled;
        }
    });
}