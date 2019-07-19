import debug                from 'debug';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import DocumentPlan         from '../nlg-blocks/Document-plan';
import DropTarget           from '../drag-in-blocks/DropTarget';
import provideAmrBlocks     from '../amr-concepts/provide-amr-blocks';
import { provideBlocks }    from '../nlg-blocks/';
import ResizableBlockly     from '../preact-blockly/Resizable';

import blockSvgOverride     from './block-svg-override';
import S                    from './NlgWorkspace.sass';
import { setCellOptions }   from './cell-options';
import toolbox              from './toolbox.xml';


const log =                 debug( 'NlgWorkspace' );


export default class NlgWorkspace extends Component {

    static propTypes = {
        amrConcepts:        PropTypes.array,
        cellNames:          PropTypes.array,
        onChangeWorkspace:  PropTypes.func,
        workspaceXml:       PropTypes.string,
    };

    Blockly =               null;
    workspace =             null;

    onChangeWorkspace = evt => {
        log( 'onChangeWorkspace', evt && evt.type, evt );
        if( this.props.onChangeWorkspace ) {

            const {
                Blockly: { Events, Xml },
                workspace,
            } = this;

            const isSameParent = (
                ( !evt.oldParentId && !evt.newParentId )
                || ( evt.oldParentId === evt.newParentId )
            );

            const shouldSkip = (
                evt.type === Events.UI
                || ( evt.type === Events.MOVE && isSameParent )
            );

            if( !shouldSkip ) {
                this.props.onChangeWorkspace({
                    documentPlan:
                        workspace
                            .getTopBlocks()
                            .find( block => block.type === DocumentPlan.type )
                            .toNlgJson(),
                    workspaceXml:
                        Xml.domToText(
                            Xml.workspaceToDom( workspace )
                        ),
                });
            }
        }
    }

    onBlockly = Blockly => {

        this.Blockly =      Blockly;

        /// Set Style for the workspace
        Blockly.HSV_SATURATION =    0.55;
        Blockly.HSV_VALUE =         0.6;

        blockSvgOverride( Blockly );
        provideBlocks( Blockly );
        provideAmrBlocks( Blockly, this.props.amrConcepts );
    };

    onWorkspace = workspace => {
        const {
            Blockly:    { Events, Xml },
            props:      { workspaceXml },
        } = this;

        this.workspace =            workspace;
        setCellOptions( this.workspace, this.props.cellNames );

        let blockIds =              [];
        if( workspaceXml ) {
            blockIds = Xml.domToWorkspace(
                Xml.textToDom( workspaceXml ),
                workspace,
            );
        }

        this.workspace.addChangeListener( evt => {
            const initialCreate = (
                evt.type === Events.CREATE
                && blockIds.length
                && blockIds.includes( evt.blockId )
            );
            /// Skip initial CREATE events:
            if( initialCreate ) {
                blockIds.splice( blockIds.findIndex( id => id === evt.blockId ));
            } else {
                this.onChangeWorkspace( evt );
            }
        });
    }

    componentWillReceiveProps( nextProps ) {
        if( this.workspace && nextProps.cellNames !== this.props.cellNames ) {
            setCellOptions( this.workspace, nextProps.cellNames );
        }
    }

    render() {
        return (
            <DropTarget
                Blockly={ this.Blockly }
                workspace={ this.workspace }
            >
                <ResizableBlockly
                    assetUrl="/blockly"
                    className={ S.blocklyWorkspace }
                    onBlockly={ this.onBlockly }
                    onWorkspace={ this.onWorkspace }
                    options={{
                        horizontalLayout:   true,
                        toolbox,
                        trashcan:           false,
                    }}
                />
            </DropTarget>
        );
    }
}
