$(document).ready(function () {

    let api_url;

    $( "#searchBtn" ).click(function() {
        let input = $( "#searchWith" ).val();
        if(input == null){
            api_url = 'http://localhost:9090/search'
        }else {
            api_url = 'http://localhost:9090/search/'+input
        }
        console.log(api_url)

        $.ajax({
            type: 'GET',
            url: api_url,
            contentType : 'application/json',
            success: function (response, status, xhr) {
                output = response;
                const hits = output.hits.hits
                let searchBy = $('#searchBy').val();
                $('#dataTable').find('tbody').empty();
                hits.forEach(hit => {
                    let ms = new Date(Date.parse(hit.source.createdAt));
                    console.log(ms);
                    $('#dataTable tbody').append("<tr><td>"+ hit.source.user.name + "</td><td>" + formatDate(hit.source.createdAt) + "</td><td>" + hit.source.text + "</td></tr>");
                })
            },
            error: function () {
                console.log('At error')
            }
        })
    });
});

function formatDate(dt) {
    return new Date(Date.parse(dt));
}


