const mealAjaxUrl = "api/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
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
                    "desk"
                ]
            ]
        })
    );
});

var currentFilterParams = {};

$(document).ready(function() {
    $(".filterForm").off().on('submit', function(event) {
        event.preventDefault();
        currentFilterParams = $(this).serialize();
        $.ajax({
            type: 'GET',
            url: ctx.ajaxUrl + 'filter',
            data: currentFilterParams,
            dataType: 'json'
        }).done(function (data) {
            ctx.datatableApi.clear().rows.add(data).draw();
            successNoty("Filtered");
        });
    });
});

function updateTableIncludingFilters() {
    var queryString = $.isEmptyObject(currentFilterParams) ? '' : 'filter?' + currentFilterParams;
    $.get(ctx.ajaxUrl + queryString, function (data) {
        ctx.datatableApi.clear().rows.add(data).draw(false);
    });
}

function clearFilter() {
    $(".filterForm")[0].reset();
    currentFilterParams = {};
    updateTable();
}
