export default Blockly => {
    /* eslint-disable */

    Blockly.BlockSvg.INLINE_PADDING_Y =     8;
    Blockly.BlockSvg.MIN_BLOCK_Y =          31;
    Blockly.BlockSvg.SEP_SPACE_X =          10;
    Blockly.BlockSvg.START_HAT =            true;

    Blockly.FieldDropdown.DROPDOWN_HEIGHT = 25;

    /**
     * Render the right side of an inline row on a block.
     * @param {!Blockly.BlockSvg.PathObject} pathObject The object containing
     *     partially constructed SVG paths, which will be modified by this function.
     * @param {!Array.<!Object>} row An object containing position information about
     *     inputs on this row of the block.
     * @param {!Object} cursor An object containing the position of the cursor,
     *     which determines where to start laying out fields.
     * @param {!Object} connectionPos An object containing the position of the
     *     connection on this input.
     * @param {number} rightEdge The position of the right edge of the block, which
     *     is based on the widest row that has been encountered so far.
     * @private
     */
    Blockly.BlockSvg.prototype.renderExternalValueInput_ = function(pathObject, row,
        cursor, connectionPos, rightEdge) {
      var steps = pathObject.steps;
      var highlightSteps = pathObject.highlightSteps;
      // External input.
      var input = row[0];
      var fieldX = cursor.x;
      var fieldY = cursor.y;
      if (input.align != Blockly.ALIGN_LEFT) {
        var fieldRightX = rightEdge - input.fieldWidth -
            Blockly.BlockSvg.TAB_WIDTH - 2 * Blockly.BlockSvg.SEP_SPACE_X;
        if (input.align == Blockly.ALIGN_RIGHT) {
          fieldX += fieldRightX;
        } else if (input.align == Blockly.ALIGN_CENTRE) {
          fieldX += fieldRightX / 2;
        }
      }
      this.renderFields_(input.fieldRow, fieldX, fieldY);
      /// Tokenmill added "tabInset":
      var tabInset = (
          input.fieldWidth
            ? 0
            : (
              rightEdge
              - input.fieldWidth
              - Blockly.BlockSvg.TAB_WIDTH
              - 3 * Blockly.BlockSvg.SEP_SPACE_X
            )
      );
      tabInset && steps.push('h', -tabInset);
      steps.push(Blockly.BlockSvg.TAB_PATH_DOWN);
      var v = row.height - Blockly.BlockSvg.TAB_HEIGHT;
      steps.push('v', v);
      tabInset && steps.push('h', tabInset);
      if (this.RTL) {
        // Highlight around back of tab.
        tabInset && highlightSteps.push('h', -tabInset);
        highlightSteps.push(Blockly.BlockSvg.TAB_PATH_DOWN_HIGHLIGHT_RTL);
        highlightSteps.push('v', v + 0.5);
        tabInset && highlightSteps.push('h', tabInset);
      } else if ( !tabInset ) {
        // Short highlight glint at bottom of tab.
        highlightSteps.push('M', (rightEdge - 5) + ',' +
            (cursor.y + Blockly.BlockSvg.TAB_HEIGHT - 0.7));
        highlightSteps.push('l', (Blockly.BlockSvg.TAB_WIDTH * 0.46) +
            ',-2.1');
      }
      // Create external input connection.
      connectionPos.x = (
          this.RTL
            ? ( -rightEdge + tabInset - 1 )
            : ( rightEdge - tabInset + 1 )
      );
      input.connection.setOffsetInBlock(connectionPos.x, cursor.y);
      if (input.connection.isConnected()) {
        this.width = Math.max(this.width, rightEdge +
            input.connection.targetBlock().getHeightWidth().width -
            Blockly.BlockSvg.TAB_WIDTH + 1);
      }
    };

    /**
     * Renders the selected option, which must be text.
     * @private
     */
    Blockly.FieldDropdown.prototype.renderSelectedText_ = function() {
      // Text option is selected.
      // Replace the text.
      var textNode = document.createTextNode(this.getDisplayText_());
      this.textElement_.appendChild(textNode);
      // Insert dropdown arrow.
      if (this.sourceBlock_.RTL) {
        this.textElement_.insertBefore(this.arrow_, this.textElement_.firstChild);
      } else {
        this.textElement_.appendChild(this.arrow_);
      }
      this.textElement_.setAttribute('text-anchor', 'start');
      /// Tokenmill replaced:
      /// this.textElement_.setAttribute('x', 0);
      this.textElement_.setAttribute('x', Blockly.BlockSvg.SEP_SPACE_X / 2 );
      /// Tokenmill added:
      this.textElement_.setAttribute('y', Blockly.FieldDropdown.DROPDOWN_HEIGHT / 2 );

      /// Tokenmill replaced:
      /// this.size_.height = Blockly.BlockSvg.MIN_BLOCK_Y;
      /// this.size_.width = Blockly.Field.getCachedWidth(this.textElement_);
      this.size_.height = Blockly.FieldDropdown.DROPDOWN_HEIGHT;
      this.size_.width = (
          Blockly.Field.getCachedWidth(this.textElement_)
          + Blockly.BlockSvg.SEP_SPACE_X
      );
    };

    const old_FieldDropdown_render = Blockly.FieldDropdown.prototype.render_;
    Blockly.FieldDropdown.prototype.render_ = function () {

        old_FieldDropdown_render.call( this );
        this.borderRect_.setAttribute( 'width', this.size_.width );
    };

    /**
     * Install this field on a block.
     */
    Blockly.Field.prototype.init = function() {
      if (this.fieldGroup_) {
        // Field has already been initialized once.
        return;
      }
      // Build the DOM.
      this.fieldGroup_ = Blockly.utils.createSvgElement('g', {}, null);
      if (!this.visible_) {
        this.fieldGroup_.style.display = 'none';
      }
      this.borderRect_ = Blockly.utils.createSvgElement('rect',
          {
            'rx':       4,
            'ry':       4,
            'x':        0, //-Blockly.BlockSvg.SEP_SPACE_X / 2,
            'y':        0,
            'height':   16
          }, this.fieldGroup_);
      /** @type {!Element} */
      this.textElement_ = Blockly.utils.createSvgElement('text', {
          'class':  'blocklyText',
          'x':      Blockly.BlockSvg.SEP_SPACE_X / 2,
          'y':      ( this.size_.height / 2 - 3 ),
      },
          this.fieldGroup_);

      this.updateEditable();
      this.sourceBlock_.getSvgRoot().appendChild(this.fieldGroup_);
      this.mouseDownWrapper_ =
          Blockly.bindEventWithChecks_(
              this.fieldGroup_, 'mousedown', this, this.onMouseDown_);
      // Force a render.
      this.render_();
    };


    /**
     * Updates thw width of the field. This calls getCachedWidth which won't cache
     * the approximated width on IE/Edge when `getComputedTextLength` fails. Once
     * it eventually does succeed, the result will be cached.
     */
    Blockly.Field.prototype.updateWidth = function() {
      var width = Blockly.Field.getCachedWidth(this.textElement_);
      if (this.borderRect_) {
        this.borderRect_.setAttribute('width',
            width + Blockly.BlockSvg.SEP_SPACE_X
        );
        this.size_.width = width + Blockly.BlockSvg.SEP_SPACE_X;
      } else {
        this.size_.width = width;
      }
    };
}
