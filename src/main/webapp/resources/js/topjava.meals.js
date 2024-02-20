const mealAjaxUrl = "profile/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: mealAjaxUrl + "filter",
            data: $("#filter").serialize()
        }).done(updateTableByData);
    }
};

$(document).ready(function () {
    $('#dateTime').datetimepicker({
        format: 'Y-m-d H:i',
    });
    $('#startDate, #endDate').datetimepicker({
        format: 'Y-m-d',
        timepicker: false
    });
    $('#startTime, #endTime').datetimepicker({
        format: 'H:i',
        datepicker: false
    });
});

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type) {
                        if (type === "display" && data) {
                            return data.replace("T", " ").substring(0, 16);
                        }
                        return data;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "createdRow": function (data, row) {
                $(data).addClass(row.excess ? 'excess' : 'non-excess');
            },
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});
