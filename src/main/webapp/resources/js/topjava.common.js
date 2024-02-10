let form;

function makeEditable(datatableApi) {
    ctx.datatableApi = datatableApi;
    form = $('#detailsForm');
    $(".delete").click(function () {
        if (confirm('Are you sure?')) {
            deleteRow($(this).closest('tr').attr("id"));
        }
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});
}

function add() {
    form.find(":input").val("");
    $("#editRow").modal();
}

function deleteRow(id) {
    $.ajax({
        url: ctx.ajaxUrl + id,
        type: "DELETE"
    }).done(function () {
        updateTable();
        successNoty("Deleted");
    });
}

function updateTable() {
    var queryString = $.isEmptyObject(currentFilterParams) ? '' : 'filter?' + $.param(currentFilterParams);
    $.get(ctx.ajaxUrl + queryString, function (data) {
        ctx.datatableApi.clear().rows.add(data).draw(false);
    });
}

function save() {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

var currentFilterParams = {};

function doFilter() {
    $(".filterForm").submit(function (event) {
        event.preventDefault();
        currentFilterParams = {
            startDate: $(this).find("input[name='startDate']").val(),
            startTime: $(this).find("input[name='startTime']").val(),
            endDate: $(this).find("input[name='endDate']").val(),
            endTime: $(this).find("input[name='endTime']").val()
        };
        var queryString = $.param(currentFilterParams);
        $.ajax({
            type: 'GET',
            url: ctx.ajaxUrl + 'filter?' + queryString,
            dataType: 'json'
        }).done(function (data) {
            ctx.datatableApi.clear().rows.add(data).draw();
            successNoty("Filtered");
        })
    });
}

function activate(checkboxElem, id) {
    var isEnabled = checkboxElem.checked;
    var row = $(checkboxElem).closest('tr');

    $.ajax({
        url: ctx.ajaxUrl + id + '/enable',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({enabled: isEnabled}),
        success: function () {
            console.log('User status updated successfully');
            if (isEnabled) {
                row.removeClass('inactive-user');
            } else {
                row.addClass('inactive-user');
            }
        },
        error: function (error) {
            console.error('Error updating user status:', error);
            checkboxElem.checked = !isEnabled;
        }
    });
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + text,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;Error status: " + jqXHR.status,
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show()
}