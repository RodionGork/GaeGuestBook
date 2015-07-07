function updateRecords(data) {
    var records = data.records;
    var div = $('#records');
    div.empty();
    for (var i in records) {
        var rec = records[i];
        $('<div/>').attr('data-id', rec.key).text(atob(rec.text)).appendTo(div);
    }
}

function loadRecords() {
    $('#records').text('Please wait...');
    $.getJSON("./api", updateRecords);
}

function sendMessage() {
    var msg = btoa($('#message').val());
    $.post("./api", msg, function() {loadRecords()}, 'text');
}

function deleteRecord(id) {
    $.ajax({
        url: "./api?id=" + id,
        type:'DELETE',
        success: function(data) {console.log(JSON.stringify(data))}
    });
}

loadRecords();
