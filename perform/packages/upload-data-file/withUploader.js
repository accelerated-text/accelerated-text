import { h, Component }     from 'preact';

import composeContexts      from '../compose-contexts';
import DocumentPlansContext from '../document-plans/Context';
import { getFileById }      from '../data-samples/functions';
import { getPlanByUid }     from '../document-plans/functions';
import {
    getDataFile,
    listDataFiles,
}                           from '../graphql/queries.graphql';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import PlanActions          from '../document-plans/Actions';
import USER                 from '../user/';
import { withClient }       from '../graphql/';

import uploadToS3           from './upload-to-s3';


const BLOCKED_ERROR =       'Will not start a new request while the previous one is not finished. Please wait.';


export default ChildComponent =>
    PlanActions( withClient( composeContexts({
        documentPlans:      DocumentPlansContext,
        openedPlan:         OpenedPlanContext,
    })( class UploadDataFileWithUploader extends Component {

        state = {
            counter:        0,
            error:          null,
            fileKey:        null,
            forPlanUid:     null,
            loading:        false,
        };

        onUploadError = error => {
            this.setState({
                error,
                fileKey:    null,
                forPlanUid: null,
                loading:    false,
            });
        };

        onUploadFileSuccess = () => {
            this.setState( state => ({
                counter:    state.counter + 1,
                error:      null,
                loading:    false,
            }));
        };

        onUploadSyncSuccess = () => {
            this.setState({
                fileKey:    null,
                forPlanUid: null,
            });
        };

        onUpload = async inputFile => {
            if( this.state.loading ) {
                this.setState({ error: BLOCKED_ERROR });
            } else {
                const { client } =          this.props;
                const fileKey =             `${ USER.id }/${ inputFile.name }`;
                const { plan } =            this.props.openedPlan;
                this.setState({
                    fileKey,
                    forPlanUid:             plan.uid,
                    loading:                true,
                });

                try {
                    await uploadToS3( fileKey, inputFile );
                    this.onUploadFileSuccess();
                    const { data, error } =
                        await client.query({
                            fetchPolicy:    'network-only',
                            query:          listDataFiles,
                        });
                    if( error ) {
                        throw Error( error );
                    }

                    const isInList = getFileById(
                        data.listDataFiles,
                        fileKey,
                    );
                    const currentPlan = getPlanByUid(
                        this.props.documentPlans.plans,
                        plan.uid,
                    );

                    if( ! isInList ) {
                        throw Error( 'Failed to update the data file list.' );
                    } else if( ! currentPlan ) {
                        throw Error( 'Document plan is gone' );
                    } else if( currentPlan.dataSampleId !== fileKey ) {
                        this.props.onUpdatePlan({
                            ...currentPlan,
                            dataSampleId:   fileKey,
                            dataSampleRow:  0,
                        });
                    }
                    this.onUploadSyncSuccess();
                    const dataFile = await client.query({
                        fetchPolicy:        'network-only',
                        query:              getDataFile,
                        variables: { id:    fileKey },
                    });
                    if( dataFile.data ) {
                        this.props.onUploadDone();
                    } else {
                        throw dataFile.error;
                    }
                } catch( err ) {
                    this.onUploadError( err );
                }
            }
        };

        render( props, state ) {
            return (
                <ChildComponent
                    onUpload={ this.onUpload }
                    uploader={ state }
                    { ...props }
                />
            );
        }
    })));
