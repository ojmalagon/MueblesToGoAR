console.log('selection: ' + selectionData.selection);
console.log('y-bearing: ' + selectionData.bearingN);
console.log('x-bearing: ' + selectionData.bearingE);
if ( !window.requestAnimationFrame ) {

			window.requestAnimationFrame = ( function() {

				return window.webkitRequestAnimationFrame ||
				window.mozRequestAnimationFrame ||
				window.oRequestAnimationFrame ||
				window.msRequestAnimationFrame ||
				function( /* function FrameRequestCallback */ callback, /* DOMElement Element */ element ) {

					window.setTimeout( callback, 1000 / 60 );

				};

			} )();

		}

var World = {
	loaded: false,
	rotating: false,
	jsonData: undefined,
	lastTouch: {
		x: 0,
		y: 0
	},
	rotateOrTranslate: 'translate',
	interactionContainer: 'gestureContainer',
	previousOrientation: undefined,
	helpMessageShows: true,
	externalBearing: 0,

    /***********************/
    input: {dragStartX:0, dragStartY:0, dragX:0, dragY:0, dragDX:0, dragDY:0, dragging:false, touchStartDistance:0, touchStartAngle:0, startScale:0, startAngle:0, pointers:[]},
    //modelContainer:
    //prefixedTransform;
    currentScale: 0.05,
    currentRotation: 0,
    posX:0,
    posY:0,
    velocityX:0,
    velocityY:0,
    containerWidth:1600,
    containerHeight: 1400,
    plateWidth:1400,
    plateHeight:1400,
    maxScale:1.5,
    minScale:0.1,
    modelPaths:['models/cama1.wt3','models/sofa1.wt3','models/sofa2.wt3','models/sofa3.wt3','models/sofa4.wt3'],

    /********************************/

	init: function initFn(filename) {
		this.createModelAtLocation(filename);
	},

	createModelAtLocation: function createModelAtLocationFn(filename) {

        var tracker  = new AR.InstantTracker({
            onChangedState:  function onChangedStateFn(state) {
            },
            deviceHeight: 1.0
        });
        var crossHairsRedImage = new AR.ImageResource("images/crosshairs_red.png");
        var crossHairsRedDrawable = new AR.ImageDrawable(crossHairsRedImage, 1.0);
        var crossHairsBlueImage = new AR.ImageResource("images/crosshairs_blue.png");
        var crossHairsBlueDrawable = new AR.ImageDrawable(crossHairsBlueImage, 1.0);
        var instantTrackable = new AR.InstantTrackable(tracker, {
            drawables: {
                    cam: crossHairsBlueDrawable,
                    initialization: crossHairsRedDrawable
            },
            onTrackingPlaneDragBegan: function onTrackingPlaneDragBeganFn(xPos, yPos) {
                    World.updatePlaneDrag(xPos, yPos);
            },
            onTrackingPlaneDragChanged: function onTrackingPlaneDragChangedFn(xPos, yPos) {
                    World.updatePlaneDrag(xPos, yPos);
            },
            onTrackingPlaneDragEnded: function onTrackingPlaneDragEndedFn(xPos, yPos) {
                    World.updatePlaneDrag(xPos, yPos);
                    World.initialDrag = false;
            }

        });/**/
		//var location = new AR.RelativeLocation(null, selectionData.bearingN * 50, selectionData.bearingE * 50, 0);
        //var geoLoc = new AR.GeoLocation(locationLat,locationLon);
		var location = new AR.RelativeLocation(null,selectionData.bearingN * 20,selectionData.bearingE * 20,0);
		World.model3DObj = new AR.Model('models/'+filename + '.wt3', {
			onLoaded: this.worldLoaded,
			scale: {
				x: World.currentScale,
				y: World.currentScale,
				z: World.currentScale
			}
		});
        var obj = new AR.GeoObject(location, {
            drawables: {
               cam: [this.model3DObj]
            }
        });
        //World.input.pointers=[];
        World.addInteractionEventListener();
        World.posX= World.containerWidth*0.005;
        World.posY= World.containerHeight*0.005;
        World.onAnimationFrame();

	},

	worldLoaded: function worldLoadedFn() {
		World.loaded = true;
		var e = document.getElementById('loadingMessage');
		e.parentElement.removeChild(e);
	},

	toggleHelpMessage: function() {
		console.log('toggleHelpMessage called');
		var helpMessageElement = document.getElementById('help_panel');

		if(World.helpMessageShows){
			helpMessageElement.style.display = 'none';
		}
		else{
			helpMessageElement.style.display = 'block';
		}

		World.helpMessageShows = !(World.helpMessageShows);
		console.log('helpMessageShows: ' + World.helpMessageShows);
		World.addInteractionEventListener();
	},
    onAnimationFrame: function onAnimationFrameFn()  {

			window.requestAnimationFrame(  World.onAnimationFrame );

			if(World.input.dragDX !== 0) World.velocityX = World.input.dragDX;
			if(World.input.dragDY !== 0) World.velocityY = World.input.dragDY;

			World.posX-= World.velocityX*0.01;
			//console.log(' velocityX'+World.velocityX);
			World.posY-= World.velocityY*0.01;
            //console.log(' velocityY'+World.velocityY);
			//restict horizontally
			if(World.posX<0) World.posX=0;
			else if(World.posX>World.containerWidth) World.posX=World.containerWidth*1;

			//restict vertically
			if(World.posY<0) World.posY=0;
			else if(World.posY>World.containerHeight) World.posY=World.containerHeight*1;

			//set the transform
			//plateContainer.style[prefixedTransform]= 'translate('+posX+'px,'+posY+'px) rotate('+currentRotation+'deg) scale('+currentScale+') translateZ(0)';
            //onsole.log(' UPDATING....... translate('+World.posX+'px,'+World.posY+'px) rotate('+World.currentRotation+'deg) scale('+World.currentScale+') translateZ(0)')

            //World.model3DObj.rotate.heading +=World.currentRotation;
            //World.model3DObj.rotate.tilt +=World.currentRotation;

            World.model3DObj.scale.x = World.currentScale;
            World.model3DObj.scale.y = World.currentScale;
            World.model3DObj.scale.z = World.currentScale;

            World.model3DObj.translate.x = World.posX;
            World.model3DObj.translate.y = World.posY;
            /*
            World.model3DObj.rotate.heading += (movement.x * 0.3);
            World.model3DObj.rotate.tilt += (movement.y * 0.3);
            */
			World.velocityX= World.velocityX*0.8;
			World.velocityY= World.velocityY*0.8;

			World.input.dragDX=0;
			World.input.dragDY=0;

	},
	/*-----------------------------------------*/
    changeTrackerState: function changeTrackerStateFn() {
        if (World.tracker.state === AR.InstantTrackerState.INITIALIZING) {
            World.tracker.state = AR.InstantTrackerState.TRACKING;
        } else {
            World.tracker.state = AR.InstantTrackerState.INITIALIZING;
        }
    },
    changeTrackingHeight: function changeTrackingHeightFn(height) {
        World.tracker.deviceHeight = parseFloat(height);
    },
    addModel: function addModelFn(pathIndex, xpos, ypos) {
       if (World.isTracking()) {
               var modelIndex = rotationValues.length;
               World.addModelValues();

               var model = new AR.Model(World.modelPaths[pathIndex], {
                   scale: {
                       x: defaultScaleValue,
                       y: defaultScaleValue,
                       z: defaultScaleValue
                   },
                   translate: {
                       x: xpos,
                       y: ypos
                   },
                   onDragChanged: function(relativeX, relativeY, intersectionX, intersectionY) {
                       this.translate = {x:intersectionX, y:intersectionY};
                   },
                   onRotationChanged: function(angleInDegrees) {
                       this.rotate.z = rotationValues[modelIndex] - angleInDegrees;
                   },
                   onRotationEnded: function(angleInDegrees) {
                      rotationValues[modelIndex] = this.rotate.z
                   },
                   onScaleChanged: function(scale) {
                       var scaleValue = scaleValues[modelIndex] * scale;
                       this.scale = {x: scaleValue, y: scaleValue, z: scaleValue};
                   },
                   onScaleEnded: function(scale) {
                       scaleValues[modelIndex] = this.scale.x;
                   }
               })

               allCurrentModels.push(model);
               lastAddedModel = model;
               this.instantTrackable.drawables.addCamDrawable(model);
           }
    },
    isTracking: function isTrackingFn() {
        return (this.tracker.state === AR.InstantTrackerState.TRACKING);
    },
    onDragChanged: function(relativeX, relativeY, intersectionX, intersectionY) {
        if (oneFingerGestureAllowed) {
            this.translate = {x:intersectionX, y:intersectionY};
        }
    },
    onDragBegan: function(x, y) {
        oneFingerGestureAllowed = true;
    },
	/*------------------------------------------*/
	handleTouchStart: function handleTouchStartFn(event,d) {
        console.log(' called handleTouchStart');
        //console.log(' called d:'+d );
        event.preventDefault();
        //var touches = event.changedTouches;
        var touches = event.targetTouches;
        console.log('handleTouchStart toches:'+ touches.length );
       console.log(' handleTouchStart eventClientX:'+ touches[0].clientX+' ;eventClientY:'+ touches[0].clientY );
       console.log(' handleTouchStart eventClientX:'+ touches[0].pageX+' ;eventClientY:'+ touches[0].pageY );
        if( touches.length === 1)
        {
            document.getElementById(World.interactionContainer).addEventListener('touchmove', World.handleTouchMove, false)
            document.getElementById(World.interactionContainer).addEventListener('touchend', World.handleTouchEnd, false)
            document.getElementById(World.interactionContainer).addEventListener('touchcancel', World.handleTouchEnd, false)
            World.handleDragStart(touches[0].clientX , touches[0].clientY);
            //World.handleGestureStart(touches[0].clientX, touches[0].clientY, touches[0].clientX, touches[0].clientY );

        }
        else if(touches.length === 2 )
        {
            World.handleGestureStart(touches[0].clientX, touches[0].clientY, touches[1].clientX, touches[1].clientY );
        }


	},
    /********************************/
    handleTouchMove: function handleTouchMoveFn(event) {
    console.log(' called handleTouchMove');
    console.log(' handleTouchMove touches:'+event.touches.length);
        event.preventDefault();
        if( event.touches.length  === 1){
           // World.handleDragStart(touches[0].clientX , touches[0].clientY);
            World.handleDragging(event.touches[0].clientX, event.touches[0].clientY);

        }else if( event.touches.length === 2 ){
            //World.handleGestureStart(touches[0].clientX, touches[0].clientY, touches[1].clientX, touches[1].clientY );
            World.handleGesture(event.touches[0].clientX, event.touches[0].clientY, event.touches[1].clientX, event.touches[1].clientY );
        }
    },
    handleTouchEnd: function handleTouchEndFn(event) {
        event.preventDefault();
        if( event.touches.length  === 0 &&  World.input.dragging){
            World.handleDragStop();
            document.getElementById(World.interactionContainer).removeEventListener('touchmove', World.handleTouchMove, false);
            document.getElementById(World.interactionContainer).removeEventListener('touchend', World.handleTouchEnd, false);
            document.getElementById(World.interactionContainer).removeEventListener('touchcancel', World.handleTouchEnd, false);
    	}else if(event.touches.length === 1 ){
            World.handleGestureStop();
            World.handleDragStart(event.touches[0].clientX, event.touches[0].clientY);
        }
    },
    indexOfPointer: function indexOfPointerFn(pointerId){
        for (var i=0;i< World.input.pointers.length;i++){
            if( World.input.pointers[i].pointerId === pointerId) {
                return i;
            }
        }
        return -1;
    },
    pointerDownHandler: function pointerDownHandlerFn(event) {
          var pointerIndex=World.indexOfPointer(event.pointerId);
          if(pointerIndex<0){
            World.input.pointers.push(event);
          }else{
            World.input.pointers[pointerIndex] = event;
          }
          if( World.input.pointers.length === 1){
    				World.handleDragStart(World.input.pointers[0].clientX , World.input.pointers[0].clientY);
    				document.getElementById(World.interactionContainer).addEventListener('pointermove', World.pointerMoveHandler, false);
    				document.getElementById(World.interactionContainer).addEventListener('pointerup', World.pointerUpHandler, false);
    			}else if( input.pointers.length === 2 ){
    				World.handleGestureStart(World.input.pointers[0].clientX, World.input.pointers[0].clientY, World.input.pointers[1].clientX, World.input.pointers[1].clientY );
    			}
    },
    pointerMoveHandler: function pointerMoveHandlerFn(event) {
    	    var pointerIndex=World.indexOfPointer(event.pointerId);
          if(pointerIndex<0){
            World.input.pointers.push(event);
          }else{
            World.input.pointers[pointerIndex] = event;
          }

          if( World.input.pointers.length  === 1){
    				World.handleDragging(World.input.pointers[0].clientX, World.input.pointers[0].clientY);
    			}else if( World.input.pointers.length === 2 ){
            console.log(World.input.pointers[0], input.pointers[1]);
    				World.handleGesture(World.input.pointers[0].clientX, World.input.pointers[0].clientY, World.input.pointers[1].clientX, World.input.pointers[1].clientY );
    			}
    },
    pointerUpHandler: function pointerUpHandlerFn(event) {
          var pointerIndex=World.indexOfPointer(event.pointerId);
          if(pointerIndex<0){

          }else{
            World.input.pointers.splice(pointerIndex,1);
          }

    			if( World.input.pointers.length  === 0 && World.input.dragging){
    				World.handleDragStop();
    				document.getElementById(World.interactionContainer).removeEventListener('pointermove', World.pointerMoveHandler, false);
                    document.getElementById(World.interactionContainer).removeEventListener('pointerup', World.pointerUpHandler, false);
    			}else if(World.input.pointers.length === 1 ){
    				World.handleGestureStop();
    				World.handleDragStart(World.input.pointers[0].clientX, World.input.pointers[0].clientY);
    			}

    },
    handleDragStart: function handleDragStartFn(x ,y ){
    			World.input.dragging = true;
    			World.input.dragStartX = World.input.dragX = x;
    			World.input.dragStartY = World.input.dragY = y;
    },
    handleDragging: function handleDraggingFn(x ,y ){
    console.log('verX:'+x+' ;verY:'+y+' ;inputDragX:'+World.input.dragX+' ;inputDargY:'+World.input.dragY)
    			if(World.input.dragging) {
    				World.input.dragDX = x-World.input.dragX;
    				World.input.dragDY = y-World.input.dragY;
    				World.input.dragX = x;
    				World.input.dragY = y;
    			}
    },
    handleDragStop: function handleDragStopFn(){
    			if(World.input.dragging) {
    				World.input.dragging = false;
    				World.input.dragDX=0;
    				World.input.dragDY=0;
    			}
    },
    handleGestureStart: function handleGestureStartFn(x1, y1, x2, y2){
    			World.input.isGesture = true;
    			//calculate distance and angle between fingers
    			var dx = x2 - x1;
    			var dy = y2 - y1;
    			World.input.touchStartDistance=Math.sqrt(dx*dx+dy*dy);
    			World.input.touchStartAngle=Math.atan2(dy,dx);
    			//we also store the current scale and rotation of the actual object we are affecting. This is needed because to enable incremental rotation/scaling.
    			World.input.startScale=World.currentScale;
    			World.input.startAngle=World.currentRotation;
    },
    handleGesture: function handleGestureFn(x1, y1, x2, y2){

    console.log(' handleGesture x1:'+x1+' ;y1:'+y1+' ;x2:'+x2+' ;y2:'+y2)
    			if(World.input.isGesture){
    				//calculate distance and angle between fingers
    				var dx = x2 - x1;
    				var dy = y2 - y1;
    				var touchDistance=Math.sqrt(dx*dx+dy*dy);
    				console.log(' handleGesture touchDistance:'+touchDistance)
    				var touchAngle=Math.atan2(dy,dx);
    				//calculate the difference between current touch values and the start values
    				var scalePixelChange = touchDistance - World.input.touchStartDistance;
    				console.log(' handleGesture scalePixelChange:'+scalePixelChange)
    				var angleChange = touchAngle - World.input.touchStartAngle;
    				//calculate how much this should affect the actual object
    				World.currentScale = World.input.startScale + scalePixelChange*0.001;
    				 console.log(' handleGesture currentScale:'+World.currentScale)
    				World.currentRotation=World.input.startAngle+(angleChange*180/Math.PI);
    				 console.log(' handleGesture currentRotation:'+World.currentRotation)
    				if(World.currentScale<World.minScale) World.currentScale=World.minScale;
    				if(World.currentScale>World.maxScale) World.currentScale=World.maxScale;
    				//World.input.startScale=World.currentScale;
    				console.log(' handleGesture startScale:'+World.input.startScale)
    			}
    },
    handleGestureStop: function handleGestureStopFn(){
    console.log(' handleGestureStop')
    			World.input.isGesture= false;
    },
    /*********************************/
    setBearingExternally: function(bearing) {
    		World.externalBearing = bearing;
    //		console.log("World.externalBearing: " + World.externalBearing);
    },

	addInteractionEventListener: function addInteractionEventListenerFn() {
		//document.getElementById("rotate_translate_anchor").addEventListener("click", World.rotateTranslateToggle);
		//document.getElementById("raise_anchor").addEventListener("click", World.raiseButton);
		//document.getElementById("lower_anchor").addEventListener("click", World.lowerButton);
		//window.addEventListener("resize", World.checkOrientation, false);
		//window.addEventListener("orientationchange", World.checkOrientation, false);
		/*-----------------------------*/
		//document.getElementById("tracking-model-button-clock").addEventListener('touchstart', function(ev){ World.requestedModel = 0;}, false);
        document.getElementById("tracking-model-button-couch").addEventListener('onstart', function(ev){ World.requestedModel = 1;}, false);
        /*document.getElementById("tracking-model-button-chair").addEventListener('touchstart', function(ev){ World.requestedModel = 2;}, false);
        document.getElementById("tracking-model-button-table").addEventListener('touchstart', function(ev){ World.requestedModel = 3;}, false);
        document.getElementById("tracking-model-button-trainer").addEventListener('touchstart', function(ev){World.requestedModel = 4;}, false);*/
		/*----------------------------*/

		document.getElementById("help_button_anchor").addEventListener("click", World.toggleHelpMessage);
		if(!World.helpMessageShows){
			document.getElementById(World.interactionContainer).addEventListener('touchstart', World.handleTouchStart, false);
           // document.getElementById(World.interactionContainer).addEventListener('touchmove', World.handleTouchMove, false);
		}
		if(World.helpMessageShows){
			document.getElementById("close_x").addEventListener("click", World.toggleHelpMessage);
			document.getElementById(World.interactionContainer).removeEventListener('touchstart', World.handleTouchStart, false);
            document.getElementById(World.interactionContainer).removeEventListener('touchmove', World.handleTouchMove, false);
		}
	}

};

