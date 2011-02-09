/*
*	Master.js for Plantin Display
*
*	@author 	Jelle Versele
*	@email		jelle@siteware.be
*	@website	siteware.be / jelleversele.be
*
*/

// handle dom ready event
$(document).ready(function(){
	

	// clear fields on focus
	$('.clearfield').clearField();
	
	
	/*
	*	Scaleable slider
	*/
	$('.tab-slider-content > ul > li').width($(document).width());
	
	$(window).resize(function(){
		$('.tab-slider-content > ul > li').width($(document).width());
	});
	
	$('.tab-slider-content').scrollable().navigator({
		navi: '.tab-slider-nav > ul'												
	});
	
	
	$('.facets > li > ul').hide();
	
	$('.facets > li > span').click(function(){
		$(this).toggleClass('open').parent().find('ul').toggle();							
	});
	
	
	
	
	
});


