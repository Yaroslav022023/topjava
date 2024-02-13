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
                    "desc"
                ]
            ]
        }), updateTable
    );
});

var currentFilterParams = {};

$(document).ready(function () {
    $(".filterForm").on('submit', function (event) {
        event.preventDefault();
        currentFilterParams = $(this).serialize();
        fetchDataAndUpdateTable(currentFilterParams);
    });
});

function fetchDataAndUpdateTable(currentFilterParams) {
    var queryString = $.isEmptyObject(currentFilterParams) ? '' : 'filter?' + currentFilterParams;
    $.ajax({
        type: 'GET',
        url: ctx.ajaxUrl + queryString,
        dataType: 'json'
    }).done(function(data) {
        ctx.datatableApi.clear().rows.add(data).draw();
        successNoty("Filtered");
    });
}

const updateTableIncludingFilters = function () {
    fetchDataAndUpdateTable(currentFilterParams);
}

function clearFilter() {
    $(".filterForm")[0].reset();
    currentFilterParams = {};
    updateTable();
}
