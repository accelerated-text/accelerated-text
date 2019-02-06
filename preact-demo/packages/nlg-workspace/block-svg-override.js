export default Blockly => {
    /* eslint-disable */

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
      /// Tokenmill: inset value inputs:
      var h = (
          rightEdge
          - input.fieldWidth
          - Blockly.BlockSvg.TAB_WIDTH
          - 2 * Blockly.BlockSvg.SEP_SPACE_X
      );
      steps.push('h', -h);
      steps.push(Blockly.BlockSvg.TAB_PATH_DOWN);
      var v = row.height - Blockly.BlockSvg.TAB_HEIGHT;
      steps.push('v', v);
      steps.push('h', h);
      if (this.RTL) {
        // Highlight around back of tab.
        highlightSteps.push('h', -h);
        highlightSteps.push(Blockly.BlockSvg.TAB_PATH_DOWN_HIGHLIGHT_RTL);
        highlightSteps.push('v', v + 0.5);
        highlightSteps.push('h', h);
      } else {
        // Short highlight glint at bottom of tab.
        highlightSteps.push('M', (rightEdge - 5) + ',' +
            (cursor.y + Blockly.BlockSvg.TAB_HEIGHT - 0.7));
        highlightSteps.push('l', (Blockly.BlockSvg.TAB_WIDTH * 0.46) +
            ',-2.1');
      }
      // Create external input connection.
      /// Tokenmill: inset input connections:
      connectionPos.x = (
          this.RTL
            ? ( -rightEdge - 1 )
            : ( rightEdge - h + 1 )
      );
      input.connection.setOffsetInBlock(connectionPos.x, cursor.y);
      if (input.connection.isConnected()) {
        this.width = Math.max(this.width, rightEdge +
            input.connection.targetBlock().getHeightWidth().width -
            Blockly.BlockSvg.TAB_WIDTH + 1);
      }
    };
}
