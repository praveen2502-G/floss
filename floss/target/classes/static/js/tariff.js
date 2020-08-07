const messages = {
    TARIFF_SUCCESS: "Tariff successfully updated",
    CAPACITY_SUCCESS: "Capacity successfully updated",
};

function getInput(inputElement, clear = false) {
    let text = inputElement.val();
    if (clear) {
        inputElement.val("");
    }
    return text;
}

function showAlert(success, text) {
    $(document).scrollTop(0);
    let alertElement = success ? $(".alert-success") : $(".alert-danger");
    alertElement.find(".alertText").text(text);
    alertElement.fadeIn(500).delay(1000).fadeOut(500);
}

$(document).ready(() => {
    getTariff();
    $(".needs-validation").on("submit", event => {
        $(event.currentTarget).addClass("was-validated");
        event.preventDefault();
        if (event.currentTarget.checkValidity()) {
            $(event.currentTarget).removeClass("was-validated");
            postTariff();
        }
    });
    $(":input").bind("keyup mouseup", () => {
        let dirty = false;
        $(":input").each(function() {
            if ($(this).data("initialValue") !== $(this).val()) {
                dirty = true;
            }
        });
        $("#submit").prop("disabled", !dirty).toggleClass("disabled", !dirty);
    });
});

function getTariff() {
    $.get("tariff-data", response => {
        $("#basicFee").val(response.basicBid);
        $("#basicPeriod").val(response.basicPeriod);
        $("#extendedFee").val(response.extendedBid);
        setClean();
    });
}

function postTariff() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "tariff-data",
        data: JSON.stringify({
            basicBid: $("#basicFee").val(),
            basicPeriod: $("#basicPeriod").val(),
            extendedBid: $("#extendedFee").val()
        }),
        success: () => {
            setClean();
            showAlert(true, messages.TARIFF_SUCCESS);
        }
    });
}

function setClean() {
    $("#submit").prop("disabled", true).addClass("disabled");
    $(":input").each(function() {
        $(this).data("initialValue", $(this).val());
    });
}

$(document).ready(() => {
    getCapacity();
    $(".needs-validation").on("submit", event => {
        $(event.currentTarget).addClass("was-validated");
        event.preventDefault();
        if (event.currentTarget.checkValidity()) {
            $(event.currentTarget).removeClass("was-validated");
            postCapacity();
        }
    });
    $(":input").bind("keyup mouseup", () => {

        $(":input").each(function() {
            if ($(this).data("initialValue") !== $(this).val()) {

            }
        });
    });
});

function getCapacity() {
    $.get("capacity-data", response => {
        $("#totalcapacity").val(response.value);
        setClear();
    });
}

function postCapacity() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "capacity-data",
        data: JSON.stringify({
            value: $("#totalcapacity").val()
        }),
        success: () => {
            setClear();
            showAlert(true, messages.CAPACITY_SUCCESS);
        }
    });
}

function setClear() {
    $(":input").each(function() {
        $(this).data("initialValue", $(this).val());
    });
}