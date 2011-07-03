$(function() {
    var load = function() {
        window.location = this.href;
    };
    var prev = function() {
        $("a.page.prev").first().each(load);
    };
    var next = function() {
        $("a.page.next").first().each(load);
    }
    $(document).keyup(function (event) {
        if (event.altKey || event.ctrlKey || event.shiftKey || event.metaKey)
            return;
        if (event.keyCode == 37) {
            prev();
        } else if (event.keyCode == 39) {
            next();
        }
    });
    var orig = null;
    var container = $(".container");
    var reset = function() {
        if (orig)
            container.animate({left: "0px"}, 200);
        orig = null;
    }
    var screenW = function() {
        return window.innerWidth;
    }
    var edge = function(touch) {
        return Math.abs(touch.screenX - screenW()/2) > (3*screenW() / 8);
    };
    document.body.ontouchstart = function(e){
        if(e.touches.length == 1 && 
           e.changedTouches.length == 1 &&
           edge(e.touches[0])
          ){
            orig = e.touches[0];
            e.preventDefault();
        }
    };
    document.body.ontouchend = function(e){
        if(orig && e.touches.length == 0 && e.changedTouches.length == 1){
            var half = screenW() / 2;
            var touch = e.changedTouches[0];
            if (orig.screenX > half && touch.screenX < half
                && $(".next").length > 0) {
                container.animate(
                    {left: "-=" + half + "px"}, 100, "linear", next);
            } else if (orig.screenX < half && touch.screenX > half
                       && $(".prev").length > 0) {
                container.animate(
                    {left: "+=" + half + "px"}, 100, "linear", prev);
            } else {
                reset();
            }
        } else {
            reset();
        }
    };
    document.body.ontouchmove = function(e){
        if(e.touches.length == 1){
            var touch = e.touches[0];
            if (orig) {
                e.preventDefault();
                container.css({
                    left: (touch.clientX - orig.clientX) + "px",
                });
            }
        }
    };
});
