import {CreateConnectorRequest} from '@sovity.de/authority-portal-client';

const tag = 'ParticipantRegisterOwnConnectorPage';

export class Reset {
  static readonly type = `[${tag}] Reset`;
}

export class Submit {
  static readonly type = `[${tag}] Register Connector`;
  constructor(
    public request: CreateConnectorRequest,
    public enableForm: () => void,
    public disableForm: () => void,
  ) {}
}
