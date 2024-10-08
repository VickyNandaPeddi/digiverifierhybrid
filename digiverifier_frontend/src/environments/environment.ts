// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  //apiUrl: 'http://ec2-3-7-78-21.ap-south-1.compute.amazonaws.com:3000',
  //apiUrl: 'http://165.232.184.105:9090', 
  //apiUrl: 'http://137.184.49.113:9090',
  apiUrl: 'http://localhost:3000',
  //epfoUrl: 'http://ec2-3-7-78-21.ap-south-1.compute.amazonaws.com:7500',
  epfoUrl: 'https://api-oauth2.digiverifier.com',
  //apiUrl: 'http://ec2-35-154-251-102.ap-south-1.compute.amazonaws.com:3000',
  // apiUrl: 'http://ec2-3-7-78-21.ap-south-1.compute.amazonaws.com:3000',
  flaskurl:'https://api-oauth2.digiverifier.com', //dummy urls, not in used
  digiurl:'https://api-oauth2.digiverifier.com', //dummy urls, not in used
  production: true,
  sessionTimeOutIn:30
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * 
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
